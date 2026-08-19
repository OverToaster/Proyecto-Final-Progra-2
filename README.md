# NominaCR

Sistema de escritorio para cálculo de nómina de empresas costarricense, desarrollado en Java (Swing) para el curso **BIS04 - Programación II**.

La aplicación permite gestionar empleados, calcular la planilla mensual aplicando la normativa laboral de Costa Rica (horas extra, cargas sociales de la CCSS y el Banco Popular, impuesto sobre la renta), generar la colilla de pago en PDF y enviarla por correo electrónico.

## Funcionalidades

- Autenticación de usuarios (login).
- Registro y gestión de empleados.
- Cálculo de nómina: salario ordinario, horas extra, deducciones y aportes patronales según la normativa de Costa Rica.
- Generación de reportes de planilla en PDF (colilla del empleado y reporte del patrono).
- Envío de la colilla de pago por correo electrónico.
- Persistencia de datos en archivos de texto plano (sin base de datos).

## Arquitectura

El proyecto sigue una **arquitectura en capas**:

```
src/
├── Presentacion/          Interfaces Swing (login, menú, empleados, nómina, reportes)
│   └── controladores/     Controladores que conectan la UI con la lógica
├── Logica/                 Reglas de cálculo de nómina y autenticación
├── Entidades/               Modelos: Empleado, Nomina, DetalleNomina, Usuario
├── AccesoDatos/
│   ├── contratos/          Interfaz genérica IRepositorio<T>
│   ├── base/                ArchivoDAO<T> (persistencia genérica) e IdControl
│   └── archivo/             DAOs concretos: Empleado, Nomina, DetalleNomina, Usuario
├── Reportes/                Generación de PDF con iText
├── Correo/                   Envío de correo con JavaMail
├── Excepciones/              Excepciones propias del dominio
└── Utilidades/                Validaciones y constantes (nombres de archivo, config. de correo)
```

## Requisitos

- **JDK 22**
- NetBeans (recomendado, el proyecto trae su configuración) o Apache Ant
- Librerías externas (no se incluyen en el repositorio, ver abajo):
  - `itextpdf-5.5.13.3.jar`
  - `javax.mail-1.6.2.jar`
  - `activation-1.1.1.jar`
- Se pueden conseguir con estos links:
  - iText5:
  - https://repo1.maven.org/maven2/com/itextpdf/itextpdf/5.5.13.3/itextpdf-5.5.13.3.jar
  - JavaMail:
  - https://repo1.maven.org/maven2/com/sun/mail/javax.mail/1.6.2/javax.mail-1.6.2.jar
  - Activacion (dependencia para el JavaMail):
  - https://repo1.maven.org/maven2/javax/activation/activation/1.1.1/activation-1.1.1.jar

## Cómo ejecutarlo

1. Clonar el repositorio.
2. Crear la carpeta `itext,javaxmail/` en la raíz del proyecto (junto a `src/`) y colocar ahí los tres `.jar` mencionados arriba.
3. Abrir la carpeta `NominaCR` con NetBeans (**File → Open Project**).
4. Verificar que el proyecto compile en JDK 22 (**clic derecho → Properties → Sources**).
5. Ejecutar la clase `Presentacion.Main` (**Run → Run Project**).

Alternativamente, por línea de comandos con Ant, desde la carpeta `NominaCR/`:

```bash
ant jar
java -jar dist/NominaCR.jar
```

## Datos

Los archivos `empleados.txt`, `nominas.txt`, `detalle_nomina.txt`, `usuarios.txt` e `id_control.txt` funcionan como el "almacenamiento" del sistema (persistencia en texto plano, separado por comas). Se generan/actualizan automáticamente al usar la aplicación.

## Autores

- Adriano
- Diego

