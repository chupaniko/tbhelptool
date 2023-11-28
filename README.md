# tbhelptool

### Инструкция по сборке JAR-ника:

1. Скачайте репозиторий
2. Создайте в src/main папку с ресурсами (resources), пометьте её, как resources folder.
3. В созданную папку с ресурсами добавьте файл с именем accounts.json следующей структуры:

```
{
  "localhost8081": {
    "tenantAdminPassword": "asdfgh",
    "name": "factory1",
    "sysadminUsername": "sysadmin@gmail.com",
    "tenantAdminUsername": "tenant@gmail.com",
    "url": "http://localhost:8081",
    "sysadminPassword": "ahdf78"
  },
  "ip192_168_23_205": {
    "tenantAdminPassword": "pgjk9898",
    "name": "virtualka",
    "sysadminUsername": "admin@yandex.ru",
    "tenantAdminUsername": "service@yandex.ru",
    "url": "http://192.168.23.205:8080",
    "sysadminPassword": "h8fvehrio"
  },
  "localhost8080": {
    "tenantAdminPassword": "tenant",
    "name": "local_build",
    "sysadminUsername": "sysadmin@thingsboard.org",
    "tenantAdminUsername": "tenant@thingsboard.org",
    "url": "http://localhost:8080",
    "sysadminPassword": "sysadmin"
  }
}
```
