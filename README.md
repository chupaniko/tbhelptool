# tbhelptool

### Инструкция по сборке исполняемого файла `tbhelptool.jar`

1. Скачайте репозиторий, откройте в среде разработки (IntelliJ IDEA).
2. Создайте в `src/main` папку с ресурсами (resources), пометьте её средствами среды разработки, как resources folder.
3. В созданную папку с ресурсами добавьте файл с именем `accounts.json` следующей структуры:
    ```json
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
   Помимо этого может потребоваться перенос файла `backup.txt` в ту же директорию, где лежит `tbhelptool.jar`,
   если требуется восстановить платформу из бэкапа какой-то другой платформы, к которой так же нет доступа извне по
   HTTP. Об этом ниже.
2. В терминале командой `cd` переходим в директорию с `tbhelptool.jar` и выполняем запуск
   командой `java -jar tbhelptool.jar`.
3. Далее, следуя инструкциям в консольном меню выполняем клонирование платформ, заводим новые аккаунты для платформ, и
   т.д. Процесс клонирования платформы состоит из двух шагов:
    * Создание образа (бэкапа) платформы, который представляет из себя
      файл с расширением `.txt`, именем `rawbackup_` + псевдоним платформы, взятый по ключу `name` из
      файла `thingsboardAccounts.json`. Файл с бэкапом имеет следующую структуру:
        ```json
        {
          "devicesInfo": [
            {
              "telemetryKeys": [
                "ClassId",
                "BatteryLevel",
                "RemoteConnectionLevel",
                "TemperatureC01"
              ],
              "relationsTo": [
                {
                  "typeGroup": "COMMON",
                  "toName": null,
                  "additionalInfo": null,
                  "fromName": "MyCustomer",
                  "from": {
                    "entityType": "CUSTOMER",
                    "id": "16ec1b60-8dea-11ee-9293-1bb76538bfc4"
                  },
                  "to": {
                    "entityType": "DEVICE",
                    "id": "1734e430-8dea-11ee-9293-1bb76538bfc4"
                  },
                  "type": "Contains"
                }
              ],
              "serverAttributes": [
                { "lastUpdateTs": 1701175305271, "value": false, "key": "active" },
                {
                  "lastUpdateTs": 1701175305271,
                  "value": 1701175305269,
                  "key": "inactivityAlarmTime"
                }
              ],
              "clientAttributes": [],
              "sharedAttributes": [],
              "relationsFrom": [],
              "lastTelemetry": {
                "BatteryLevel": [{ "value": "75", "ts": 1701174704693 }],
                "RemoteConnectionLevel": [{ "value": "71", "ts": 1701174704693 }],
                "ClassId": [{ "value": "22557", "ts": 1701174704693 }],
                "TemperatureC01": [{ "value": "2372", "ts": 1701174704693 }]
              },
              "entity": {
                "additionalInfo": {
                  "description": "Предназначен для измерения температуры газообразных и жидких сред."
                },
                "tenantId": {
                  "entityType": "TENANT",
                  "id": "c1b55f70-6739-11ee-8e54-651f2e42926e"
                },
                "customerId": {
                  "entityType": "CUSTOMER",
                  "id": "16ec1b60-8dea-11ee-9293-1bb76538bfc4"
                },
                "name": "Термометр",
                "createdTime": 1701174704627,
                "id": {
                  "entityType": "DEVICE",
                  "id": "1734e430-8dea-11ee-9293-1bb76538bfc4",
                  "accessToken": "HIOUbuv89s87bkvaslsk"
                },
                "label": "Термометр",
                "type": "meter"
              }
            }
          ],
          "assetsInfo": [
            {
              "telemetryKeys": [],
              "relationsTo": [],
              "serverAttributes": [],
              "clientAttributes": [],
              "sharedAttributes": [],
              "relationsFrom": [],
              "lastTelemetry": {},
              "entity": {
                "additionalInfo": null,
                "tenantId": {
                  "entityType": "TENANT",
                  "id": "c1b55f70-6739-11ee-8e54-651f2e42926e"
                },
                "customerId": {
                  "entityType": "CUSTOMER",
                  "id": "16ec1b60-8dea-11ee-9293-1bb76538bfc4"
                },
                "name": "Report builder",
                "createdTime": 1701174704405,
                "id": {
                  "entityType": "ASSET",
                  "id": "17130450-8dea-11ee-9293-1bb76538bfc4"
                },
                "label": null,
                "type": "link"
              }
            }
          ],
          "customersInfo": [
            {
              "customerUsers": [],
              "customer": {
                "telemetryKeys": [],
                "relationsTo": [],
                "serverAttributes": [
                  {
                    "lastUpdateTs": 1701254769429,
                    "value": "78.457345",
                    "key": "latitude"
                  },
                  {
                    "lastUpdateTs": 1701254759675,
                    "value": "23.362345",
                    "key": "longitude"
                  }
                ],
                "clientAttributes": [],
                "sharedAttributes": [],
                "relationsFrom": [
                  {
                    "typeGroup": "COMMON",
                    "toName": "Термометр",
                    "additionalInfo": null,
                    "fromName": null,
                    "from": {
                      "entityType": "CUSTOMER",
                      "id": "16ec1b60-8dea-11ee-9293-1bb76538bfc4"
                    },
                    "to": {
                      "entityType": "DEVICE",
                      "id": "1734e430-8dea-11ee-9293-1bb76538bfc4"
                    },
                    "type": "Contains"
                  }
                ],
                "lastTelemetry": {},
                "entity": {
                  "zip": null,
                  "country": null,
                  "address": null,
                  "city": null,
                  "address2": null,
                  "title": "MyCustomer",
                  "phone": null,
                  "additionalInfo": null,
                  "tenantId": {
                    "entityType": "TENANT",
                    "id": "c1b55f70-6739-11ee-8e54-651f2e42926e"
                  },
                  "name": "MyCustomer",
                  "createdTime": 1701174704150,
                  "id": {
                    "entityType": "CUSTOMER",
                    "id": "16ec1b60-8dea-11ee-9293-1bb76538bfc4"
                  },
                  "state": null,
                  "email": null
                }
              }
            }
          ]
        }
        ```
        * Восстановление платформы из образа (бэкапа). При этом имя файла с бэкапом должно быть `backup.txt`. Файл с
          бэкапом должен лежать в той же директории, что и `tbhelptool.jar`.

Стоит отметить, что при первом запуске в директории с `tbhelptool.jar` будет создан файл `thingsboardAccounts.json`,
который представляет из себя копию файла `src/main/resources/accounts.json`, содержащий аккаунты платформ Thingsboard.
Поэтому нужно следить, чтобы к файлу `tbhelptool.jar` и всем связанным с ним файлами не было доступа у третьих лиц,
своевременно их удалять после использования.
