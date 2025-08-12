# Paint Contest Bot
!\[Java version\](https://badgen.net/badge/java/11/blue)
!\[Gradle version\](https://badgen.net/badge/gradle/7.4.1/blue)
!\[PRs welcome badge\](https://badgen.net/badge/PRs/welcome/green)

Telegram bot for painting competitions organized by Warhammer Community of Uzbekistan. Based on [AbilityBot](https://github.com/addo47/AbilityBots)


## Getting started:

1. copy the source
```shell
git clone https://github.com/fire-papaya/paint_contest.git
```

2. build the bot
```shell
cd paint_contest
```
and 
```shell
./gradlew clean bootJar
```
the built fat .jar will be located at `bot/build/libs` 


## Configuring and running

1. Place configuration `.yaml` file next to `.jar`. Naming convention is `application-{your-profile}.yaml` . E.g. `application-test.yaml`

```yaml
tg:
  bot:
    token: token ## telegram token here
    username: username ## your telegram username, the username will be given CREATOR rights to manage admins and AbilityBot 
    creator: 123456789  ## your telegramId

spring:
  datasource:
    driver-class-name: org.mariadb.jdbc.Driver
    url: jdbc:mariadb://localhost:3310/paint_contest  ## connection and name of the mariadb database
    username: root  ## db connection username
    password:  ## db connection password
```


2. run the bot with
```shell
java -jar -Dspring.profiles.active=test bot/build/libs/bot-0.4.1.jar
```

## Use the bot

1. to manage painting contests promote your self to Admin:
 within telegram itself send the comman:
 ```
 /promote your_telegram_username
 ```

2. in the database add in the table `community` new row for your community with the name and short code

3. find yourself (by telegramId or username) in the database and set yourself as admin (`is_admin`). Create connection in `community_admin_user`