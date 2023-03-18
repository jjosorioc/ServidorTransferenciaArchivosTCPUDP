# ISIS3204-ServidorTransferenciaArchivos

Se deberá implementar un servidor de transferencia de archivos en el lenguaje de programación de su preferencia (Java, Python, entre otros), el cual debe cumplir con los siguientes requerimientos:

1. Debe implementar el protocolo de control de transmisión (TCP por sus siglas en inglés) para transmitir un archivo hacía un cliente
2. Debe correr sobre una máquina con sistema operativo UbuntuServer 20.04
3. La aplicación debe soportar al menos 25 conexiones concurrentes.
4. Debe tener dos archivos disponibles para su envío a los clientes: un archivo de tamaño 100 MB y otro de 250 MB.
5. La aplicación debe permitir seleccionar qué archivo desea transmitir a los clientes conectados y a cuántos clientes en simultáneo; a todos se les envía el mismo archivo durante una transmisión.
6. Debe enviar a cada cliente un valor hash calculado para el archivo transmitido; este valor será usado para validar la integridad del archivo enviado en el aplicativo de cliente.
7. La transferencia de archivos a los clientes definidos en la prueba debe realizarse solo cuando el número de clientes indicados estén conectados y el estado de todos sea listo para recibir.
