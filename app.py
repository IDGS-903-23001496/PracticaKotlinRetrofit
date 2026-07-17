import os
from flask import Flask, request, jsonify
from flask_sqlalchemy import SQLAlchemy
from flask_cors import CORS

app = Flask(__name__)
CORS(app)  # Permite que la app de Android se conecte sin problemas de CORS

# Configuración de la Base de Datos SQLite local
db_path = os.path.join(os.path.abspath(os.path.dirname(__file__)), 'biblioteca.db')
app.config['SQLALCHEMY_DATABASE_URI'] = f'sqlite:///{db_path}'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

db = SQLAlchemy(app)

# ==============================================================================
# MODELO DE LA BASE DE DATOS
# ==============================================================================
class Libro(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    titulo = db.Column(db.String(150), nullable=False)
    autor = db.Column(db.String(100), nullable=False)
    isbn = db.Column(db.String(20), unique=True, nullable=False)
    ejemplares = db.Column(db.Integer, nullable=False, default=1)

    def to_dict(self):
        """Convierte el objeto en un diccionario para poder serializarlo a JSON"""
        return {
            "id": self.id,
            "titulo": self.titulo,
            "autor": self.autor,
            "isbn": self.isbn,
            "ejemplares": self.ejemplares
        }

# Crea las tablas en la base de datos si no existen
with app.app_context():
    db.create_all()

# ==============================================================================
# RUTAS DE LA API (CRUD)
# ==============================================================================

# 1. CONSULTAR TODOS LOS LIBROS (GET)
@app.route('/api/libros', methods=['GET'])
def obtener_libros():
    try:
        libros = Libro.query.all()
        return jsonify([libro.to_dict() for libro in libros]), 200
    except Exception as e:
        return jsonify({"error": "Error interno del servidor", "detalle": str(e)}), 500


# 2. AGREGAR UN LIBRO (POST)
@app.route('/api/libros', methods=['POST'])
def crear_libro():
    data = request.get_json()
    
    # Validaciones básicas del lado del servidor
    if not data or not all(k in data for k in ('titulo', 'autor', 'isbn', 'ejemplares')):
        return jsonify({"error": "Faltan campos obligatorios"}), 400
    
    # Validar que el ISBN no esté duplicado
    libro_existente = Libro.query.filter_by(isbn=data['isbn']).first()
    if libro_existente:
        return jsonify({"error": f"El ISBN '{data['isbn']}' ya se encuentra registrado"}), 400

    try:
        nuevo_libro = Libro(
            titulo=data['titulo'],
            autor=data['autor'],
            isbn=data['isbn'],
            ejemplares=int(data['ejemplares'])
        )
        db.session.add(nuevo_libro)
        db.session.commit()
        return jsonify(nuevo_libro.to_dict()), 201
    except Exception as e:
        db.session.rollback()
        return jsonify({"error": "No se pudo guardar el libro", "detalle": str(e)}), 500


# 3. MODIFICAR UN LIBRO (PUT)
@app.route('/api/libros/<int:id>', methods=['PUT'])
def actualizar_libro(id):
    libro = Libro.query.get(id)
    if not libro:
        return jsonify({"error": "Libro no encontrado"}), 404

    data = request.get_json()
    try:
        if 'titulo' in data:
            libro.titulo = data['titulo']
        if 'autor' in data:
            libro.autor = data['autor']
        if 'isbn' in data:
            # Validar ISBN duplicado solo si se está modificando a uno diferente
            if data['isbn'] != libro.isbn:
                existente = Libro.query.filter_by(isbn=data['isbn']).first()
                if existente:
                    return jsonify({"error": "El nuevo ISBN ya está registrado por otro libro"}), 400
            libro.isbn = data['isbn']
        if 'ejemplares' in data:
            libro.ejemplares = int(data['ejemplares'])

        db.session.commit()
        return jsonify(libro.to_dict()), 200
    except Exception as e:
        db.session.rollback()
        return jsonify({"error": "No se pudo actualizar el libro", "detalle": str(e)}), 500


# 4. ELIMINAR UN LIBRO (DELETE)
@app.route('/api/libros/<int:id>', methods=['DELETE'])
def eliminar_libro(id):
    libro = Libro.query.get(id)
    if not libro:
        return jsonify({"error": "Libro no encontrado"}), 404

    try:
        db.session.delete(libro)
        db.session.commit()
        return jsonify({"mensaje": f"Libro '{libro.titulo}' eliminado correctamente"}), 200
    except Exception as e:
        db.session.rollback()
        return jsonify({"error": "No se pudo eliminar el libro", "detalle": str(e)}), 500


# ==============================================================================
# RUTA DE BIENVENIDA (Para evitar el error "Not Found" en el navegador)
# ==============================================================================
@app.route('/', methods=['GET'])
def index():
    return jsonify({
        "status": "online",
        "mensaje": "Servidor de la Biblioteca funcionando correctamente.",
        "rutas_disponibles": {
            "obtener_y_crear_libros": "/api/libros [GET, POST]",
            "modificar_y_eliminar": "/api/libros/<id> [PUT, DELETE]"
        }
    }), 200


# ==============================================================================
# INICIO DE LA APLICACIÓN
# ==============================================================================
if __name__ == '__main__':
    # Escuchamos en 0.0.0.0 para que otros dispositivos (como tu celular físico o
    # el emulador de Android) en la misma red local puedan acceder a la API.
    app.run(host='0.0.0.0', port=5000, debug=True)