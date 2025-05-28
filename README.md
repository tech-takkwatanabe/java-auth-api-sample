# java-auth-api-sample

Spring Bootを使用した認証APIのサンプル実装です。

## 機能

- ユーザー登録
- ログイン（JWT認証）
- リフレッシュトークンによるアクセストークンの更新
- ログアウト
- 現在のユーザー情報の取得

## 技術スタック

- Java 17
- Spring Boot 3.2
- Spring Security
- MyBatis
- Redis
- JWT
- Maven

## セットアップ

1. 必要な環境
   - Java 17
   - Maven
   - Redis

2. アプリケーションの起動
```bash
cd apps/api
mvn spring-boot:run
```

## 開発

### コードフォーマット

```bash
mvn spotless:apply
```

### デバッグログを出力する場合

```bash
mvn spring-boot:run -X > debug.log 2>&1
```

### OpenAPI仕様の生成

アプリケーション起動後に以下のコマンドを実行：

```bash
mvn springdoc-openapi:generate
```

## API エンドポイント

### 認証関連

- `POST /api/auth/signup` - ユーザー登録
- `POST /api/auth/login` - ログイン
- `POST /api/auth/refresh` - トークン更新
- `POST /api/auth/logout` - ログアウト
- `GET /api/auth/me` - 現在のユーザー情報取得

## API ドキュメント

アプリケーション起動後、以下のURLでSwagger UIにアクセスできます：

http://localhost:8080/swagger-ui.html

## セキュリティ

- JWTを使用した認証
- パスワードのハッシュ化
- リフレッシュトークンの実装
- トークンの有効期限管理