# Delivery Application

## Descripción

Este proyecto es una aplicación de entrega de alimentos que permite a los usuarios registrarse como clientes, repartidores o restaurantes. Los clientes pueden buscar restaurantes, realizar pedidos y gestionar sus direcciones. Los restaurantes pueden gestionar sus menús y platos, mientras que los repartidores pueden ver y gestionar los pedidos pendientes.

## Estructura del Proyecto
```sh
├───.github
│   └───workflows
├───.mvn
│   └───wrapper
├───.vscode
├───database
│   ├───log
│   ├───seg0
│   └───tmp
├───src
│   ├───main
│   │   ├───java
│   │   │   └───es
│   │   │       └───uclm
│   │   │           └───delivery
│   │   │               ├───config
│   │   │               ├───dominio
│   │   │               │   ├───controladores
│   │   │               │   ├───entidades
│   │   │               │   └───excepciones
│   │   │               ├───persistencia
│   │   │               └───presentacion
│   │   └───resources
│   │       ├───static
│   │       │   ├───css
│   │       │   └───images
│   │       └───templates
│   └───test
│       └───java
│           └───es
│               └───uclm
│                   └───delivery
│                       ├───dominio
│                       │   ├───controladores
│                       │   └───entidades
│                       ├───persistencia
│                       └───presentacion
└───target
    ├───classes
    │   ├───es
    │   │   └───uclm
    │   │       └───delivery
    │   │           ├───config
    │   │           ├───dominio
    │   │           │   ├───controladores
    │   │           │   ├───entidades
    │   │           │   └───excepciones
    │   │           ├───persistencia
    │   │           └───presentacion
    │   ├───static
    │   │   ├───css
    │   │   └───images
    │   └───templates
    ├───delivery-0.0.1-SNAPSHOT
    │   ├───META-INF
    │   └───WEB-INF
    │       ├───classes
    │       │   ├───es
    │       │   │   └───uclm
    │       │   │       └───delivery
    │       │   │           ├───config
    │       │   │           ├───dominio
    │       │   │           │   ├───controladores
    │       │   │           │   ├───entidades
    │       │   │           │   └───excepciones
    │       │   │           ├───persistencia
    │       │   │           └───presentacion
    │       │   ├───static
    │       │   │   ├───css
    │       │   │   └───images
    │       │   └───templates
    │       └───lib
    ├───generated-sources
    │   └───annotations
    ├───generated-test-sources
    │   └───test-annotations
    ├───maven-archiver
    ├───maven-status
    │   └───maven-compiler-plugin
    │       ├───compile
    │       │   └───default-compile
    │       └───testCompile
    │           └───default-testCompile
    ├───site
    │   ├───css
    │   └───images
    │       └───logos
    ├───surefire-reports
    └───test-classes
        └───es
            └───uclm
                └───delivery
                    ├───dominio
                    │   ├───controladores
                    │   └───entidades
                    ├───persistencia
                    └───presentacion
```
## Instalación

1. Clona el repositorio:
```sh
    git clone <URL_DEL_REPOSITORIO>
```

2. Navega al directorio del proyecto:
```sh
    cd delivery-application
```

3. Construye el proyecto con Maven:
```sh
    mvn clean install
```

## Ejecución

Para ejecutar la aplicación, usa el siguiente comando:
```sh
    mvn spring-boot:run
```

## Pruebas

Para ejecutar las pruebas, usa el siguiente comando:
```sh
mvnw test
```
## Funcionalidades

### Clientes

- Registro y autenticación.
- Búsqueda de restaurantes por código postal.
- Realización y gestión de pedidos.
- Gestión de direcciones.
- Valoración de pedidos.

### Restaurantes

- Registro y autenticación.
- Gestión de menús y platos.
- Edición de información del restaurante.

### Repartidores

- Registro y autenticación.
- Visualización y gestión de pedidos pendientes.

## Tecnologías Utilizadas

- Java
- Spring Boot
- Maven
- H2 Database
- Thymeleaf
- CSS

## Contribuciones

Las contribuciones son bienvenidas. Por favor, abre un issue o envía un pull request para discutir cualquier cambio que desees realizar.

## Licencia

Este proyecto es un trabajo universitario y no debe ser copiado, modificado, distribuido o utilizado de ninguna manera sin el permiso expreso de los autores. El uso no autorizado de este código puede resultar en acciones legales.

Para obtener permiso para usar este código, por favor contactar a los autores.