# tbhelptool

### Инструкция по сборке исполняемого файла `tbhelptool.jar`

1. Скачайте репозиторий, откройте в среде разработки (IntelliJ IDEA).
2. Создайте в `src/main` папку с ресурсами (resources), пометьте её средствами среды разработки, как resources folder.
3. В созданную папку с ресурсами добавьте файл с именем `accounts.json` следующей структуры:
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
То есть файл должен содержать логины и пароли для пользователей с ролями `SYSADMIN` и `TENANT ADMIN`, URL платформы
и её псевдоним, который впоследствии будет включен в имя файла с бэкапом.
4. Собрать мавеном JAR-архив (команда `mvn package`) или в IntelliJ IDEA открыть Maven -> tbhelptool -> Lifecycle;
double click по package.
5. Нужный нам исполняемый файл будет иметь путь `target/tbhelptool.jar`.

### Инструкция по использованию `tbhelptool.jar`

1. `tbhelptool` должен иметь доступ по HTTP к той платформе Thingsboard, с которой он будет работать. Соответственно,
если у платформы нет белого IP, то надо перенести файл `tbhelptool.jar` на тот сервер, где развернута платформа. 
Помимо этого может потребоваться перенос файла `backup.txt` в ту же директорию, где лежит `tbhelptool.jar`, если требуется восстановить платформу из бэкапа какой-то
другой платформы, к которой так же нет доступа извне по HTTP. Об этом ниже.
2. В терминале командой `cd` переходим в директорию с `tbhelptool.jar` и выполняем запуск командой `java -jar tbhelptool.jar`.
3. Далее следуя инструкциям в консольном меню выполняем клонирование платформ, заводим новые аккаунты для платформ, и т.д.
Стоит отметить, что при первом запуске в директории с `tbhelptool.jar` будет создан файл `thingsboardAccounts.json`, 
который представляет из себя копию файла `src/main/resources/accounts.json`, содержащий аккаунты платформ Thingsboard. 
Поэтому нужно следить, чтобы к файлу `tbhelptool.jar` и всем связанным с ним файлами не было доступа у третьих лиц, 
своевременно их удалять после использования.
