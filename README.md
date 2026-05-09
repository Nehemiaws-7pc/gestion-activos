# Gestión de Activos

Un sistema completo para la gestión y control de activos empresariales, desarrollado con Java, TypeScript y tecnologías modernas.

## 📋 Descripción

Esta aplicación permite administrar, registrar y monitorear los activos de una organización, proporcionando una interfaz intuitiva y herramientas robustas para el control de inventario.

## 🛠️ Tecnologías Utilizadas

- **Java** (38.2%) - Backend y lógica de negocio
- **TypeScript** (24.7%) - Lógica frontend
- **HTML** (17.9%) - Estructura de la interfaz
- **CSS** (17.5%) - Estilos y diseño
- **Shell** (1%) - Scripts de automatización
- **Docker** (0.4%) - Containerización
- **Batch** (0.3%) - Automatización Windows

## 📦 Requisitos Previos

Antes de comenzar, asegúrate de tener instalado:

- **Git** - Control de versiones
- **Java JDK 11+** - Entorno de ejecución
- **Node.js 14+** - Gestor de paquetes npm
- **Maven 3.6+** - Gestor de dependencias Java (opcional)
- **Docker** (opcional) - Para ejecución en contenedores

## 🚀 Instalación

### 1. Clonar el Repositorio

```bash
git clone https://github.com/Nehemiaws-7pc/gestion-activos.git
cd gestion-activos
```

### 2. Instalación del Backend (Java)

Si el proyecto utiliza Maven:

```bash
# Instalar dependencias
mvn clean install

# Compilar el proyecto
mvn compile

# Ejecutar la aplicación
mvn spring-boot:run
```

O si prefieres usar Gradle:

```bash
./gradlew build
./gradlew bootRun
```

### 3. Instalación del Frontend (TypeScript/Node.js)

```bash
# Acceder al directorio del frontend (ajusta la ruta según tu estructura)
cd frontend

# Instalar dependencias
npm install

# Ejecutar en modo desarrollo
npm start

# O compilar para producción
npm run build
```

### 4. Configuración de Base de Datos

Asegúrate de configurar las variables de entorno necesarias:

```bash
# Crea un archivo .env en la raíz del proyecto (si es necesario)
cp .env.example .env

# Edita el archivo .env con tus credenciales
nano .env
```

Configura las siguientes variables:
- `DB_HOST` - Host de la base de datos
- `DB_PORT` - Puerto de la base de datos
- `DB_NAME` - Nombre de la base de datos
- `DB_USER` - Usuario de la base de datos
- `DB_PASSWORD` - Contraseña de la base de datos

### 5. Ejecución con Docker (Opcional)

Si prefieres usar Docker:

```bash
# Construir la imagen
docker build -t gestion-activos .

# Ejecutar el contenedor
docker run -p 8080:8080 gestion-activos
```

O usando Docker Compose:

```bash
docker-compose up -d
```

## 📁 Estructura del Proyecto

```
gestion-activos/
├── src/
│   ├── main/
│   │   ├── java/          # Código backend Java
│   │   ├── resources/     # Recursos de la aplicación
│   │   └── webapp/        # Archivos web (HTML, CSS, TS)
│   └── test/              # Tests
├── frontend/              # Aplicación frontend (si está separada)
├── docker/                # Configuración Docker
├── pom.xml                # Configuración Maven
├── package.json           # Dependencias Node.js
└── README.md             # Este archivo
```

## 🔧 Configuración Adicional

### Variables de Entorno

Configura las variables de entorno necesarias antes de ejecutar:

```bash
export JAVA_OPTS="-Xmx512m -Xms256m"
export NODE_ENV=production
```

### Puerto de la Aplicación

Por defecto, la aplicación se ejecuta en:
- **Backend**: `http://localhost:8080`
- **Frontend**: `http://localhost:3000`

Si necesitas cambiar los puertos, edita los archivos de configuración correspondientes.

## 📝 Scripts Útiles

Algunos scripts que pueden ser útiles:

```bash
# Ejecutar tests
mvn test

# Limpiar y compilar
mvn clean package

# Formatea el código
mvn spotless:apply

# Análisis de calidad de código
mvn sonar:sonar
```

## 🐛 Troubleshooting

### Error: "Java home not found"
Asegúrate de tener JAVA_HOME configurado correctamente:

```bash
export JAVA_HOME=/path/to/jdk
export PATH=$JAVA_HOME/bin:$PATH
```

### Error: "Port already in use"
Si el puerto 8080 está en uso, cambia el puerto en la configuración:

```properties
# application.properties
server.port=8081
```

### Error: "npm dependencies not installed"
Limpia la caché y reinicia:

```bash
cd frontend
rm -rf node_modules package-lock.json
npm install
```

## 📚 Documentación

Para más información sobre el proyecto y su documentación, consulta:
- [Wiki del Proyecto](https://github.com/Nehemiaws-7pc/gestion-activos/wiki)
- [Issues](https://github.com/Nehemiaws-7pc/gestion-activos/issues)

## 🤝 Contribuciones

Las contribuciones son bienvenidas. Por favor:

1. Fork el repositorio
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está bajo la licencia MIT. Ver el archivo `LICENSE` para más detalles.

## 👤 Autor

**Nehemiaws-7pc**

- GitHub: [@Nehemiaws-7pc](https://github.com/Nehemiaws-7pc)

## 📞 Soporte

Si encuentras problemas o tienes preguntas:

1. Revisa la sección de [Issues](https://github.com/Nehemiaws-7pc/gestion-activos/issues)
2. Crea un nuevo issue con detalles del problema
3. Incluye logs y pasos para reproducir el error

---

**Última actualización**: Mayo 2026
