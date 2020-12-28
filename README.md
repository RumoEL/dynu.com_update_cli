# dynu.com_update_cli
client for dyno ddns only with the ability to get a list of domains and update them
only:
### GET /dns

```bash
java -jar target/DynoserviceInit-git-*-jar-with-dependencies.jar --getID YOUR-API-KEY
```
### POST /dns/ID

#1 edit ./configsDynoService/config.yml (+ set prepare true)

#2 run:
```bash
java -jar target/DynoserviceInit-git-*-jar-with-dependencies.jar
```
