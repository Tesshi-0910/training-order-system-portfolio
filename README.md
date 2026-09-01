# 受発注管理システム (Training Order System)

Spring Boot で構築した、顧客・商品・注文を管理するシンプルな受発注管理Webアプリケーションです。

## 主な機能

- ユーザー登録 / ログイン認証（Spring Security, パスワードは BCrypt でハッシュ化）
- 顧客管理（一覧・登録・編集・削除）
- 商品管理（一覧・登録・編集・削除）
- 注文管理（一覧・登録・詳細表示）

## 技術スタック

| 分類 | 技術 |
| --- | --- |
| 言語 / ランタイム | Java 21 |
| フレームワーク | Spring Boot 3.4 (Web, Security, Data JPA, Validation) |
| テンプレートエンジン | Thymeleaf |
| DB | PostgreSQL |
| ビルドツール | Gradle |
| デプロイ | Docker / Render |

## ローカルでの起動方法

1. PostgreSQL を起動し、データベースを作成します。
   ```sql
   CREATE DATABASE training_order_db;
   ```
2. 接続情報を環境変数で指定して起動します（未指定の場合は `localhost:5432 / training_order_db / postgres / postgres` が使われます）。
   ```bash
   export DB_HOST=localhost
   export DB_PORT=5432
   export DB_NAME=training_order_db
   export DB_USER=postgres
   export DB_PASSWORD=your_local_password
   ./gradlew bootRun
   ```
3. ブラウザで `http://localhost:8080` にアクセスし、`/register` から新規アカウントを作成してログインします。

## デプロイ (Render)

このリポジトリには [`render.yaml`](./render.yaml) を同梱しており、Render の Blueprint 機能を使うと Web サービスと PostgreSQL データベースをまとめて自動構築できます。

1. Render (https://render.com) にサインアップし、GitHub アカウントを連携します。
2. 「New +」→「Blueprint」から、このリポジトリを選択します。
3. `render.yaml` の内容が自動で読み込まれ、Web サービスとDBが作成されます（DB接続情報の環境変数も自動設定されます）。
4. デプロイ完了後に発行される `https://xxxxx.onrender.com` のURLが、他の人と共有できる公開リンクになります。

無料プランのため、しばらくアクセスがないとサービスがスリープし、次回アクセス時に起動まで数十秒かかることがあります。

## ディレクトリ構成（抜粋）

```
src/main/java/com/example/ordersystem/
├── controller/   画面・APIのエントリポイント
├── service/      業務ロジック
├── repository/   Spring Data JPA リポジトリ
├── entity/       DBエンティティ
├── form/         画面入力フォーム
└── config/       Security / Web 設定
```
