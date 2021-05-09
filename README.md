# kotlin-clean-architecture

## 概要

Clean Architecture を意識しながら、以下のライブラリや F/W を使って適当な Web アプリを作るリポジトリ

* Ktor: Web アプリケーション構築用 F/W
* Koin: Dependency Injection 用 F/W
* Exposed: OR Mapper
* kotest: Testing F/W

## 動作環境

```text
$ java -version
openjdk version "11.0.10" 2021-01-19 LTS
OpenJDK Runtime Environment Corretto-11.0.10.9.1 (build 11.0.10+9-LTS)
OpenJDK 64-Bit Server VM Corretto-11.0.10.9.1 (build 11.0.10+9-LTS, mixed mode)

$ mvn -version
Apache Maven 3.6.3
```

## 起動

Postgres をあらかじめ起動しておく。
docker-compose が [m\-takehara/kotlin\-clean\-architecture\-environment](https://github.com/m-takehara/kotlin-clean-architecture-environment) にあるのでこれを使用する。

```text
$ mvn install
$ mvn exec:java -pl rest -Dexec.mainClass=me.takehara.rest.MainKt
# 終了時は Ctrl + C、終了に若干時間がかかる
```

## リクエスト送信

```http request
POST http://localhost:8080/users
Content-Type: application/json

{
  "name": "m-takehara",
  "password": "password",
  "mailAddress": "m-takehara@gmail.com"
}

###

GET http://localhost:8080/users/811eb3ab-24fa-4a0f-80a2-f1ac44f274f3
```
