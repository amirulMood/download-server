Note*** Application is using Springboot

1. Run the client application, it will automatically create the txt file.
2. Run the server application, it will save using /create-file
3. When both application is running, run API in postman:
   http://localhost:8080/download-file?clientUrl=http://localhost:8082/create-file
