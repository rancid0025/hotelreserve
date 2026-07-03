# ホテル予約システム (Hotel Reservation System)

UMLクラス図 (BCE: Boundary / Control / Entity) に基づいて作成したホテル予約システムです。
Waseda-SE (NII HRS 教材) のアーキテクチャ (Form=Boundary / Control / Manager+DAO=Entity) をベースにしています。
授業用に DAO をメモリ実装 (`*MemoryDao`) にしているため、**HSQLDB サーバなしでそのまま実行できます**。

## コンパイルと実行

```
cd hotel-reservation-system
javac -encoding UTF-8 -d bin src/util/*.java src/domain/*.java src/domain/*/*.java src/app/*.java src/app/*/*.java
java -cp bin app.cui.CUI
```

※ Windows のコマンドプロンプトで日本語が文字化けする場合は、実行前に `chcp 65001` を実行してください。

## クラス図との対応

### Boundary (画面・UI) → `app.*.＊Form` / `app.cui.CUI`

| クラス図 | Java クラス | メソッド対応 |
|---|---|---|
| 宿泊予約画面 | `app.reservation.ReserveRoomForm` | 部屋を選択する=`selectRoom` / 予約番号を表示する=`displayReservationNumber` |
| 宿泊部屋検索画面 | `app.search.RoomSearchForm` | 部屋検索条件を入力する=`inputSearchCondition` / 空室一覧を表示する=`displayVacantRoomList` / 警告を表示する=`displayWarning` |
| 部屋管理画面 | `app.management.RoomManagementForm` | 部屋情報を入力する=`inputRoomInfo` / 全部屋を表示する=`displayAllRooms` |
| チェックイン画面 | `app.checkin.CheckInRoomForm` | 予約番号を入力する=`inputReservationNumber` / 予約情報を表示する=`displayReservationInfo` / 予約を選択して確定する=`confirmCheckIn` |
| チェックアウト画面 | `app.checkout.CheckOutRoomForm` | 部屋番号を入力する=`inputRoomNumber` / 支払いを確定する=`confirmPayment` |

### Control (処理・コントローラ) → `app.*.＊Control`

| クラス図 | Java クラス | メソッド対応 |
|---|---|---|
| 宿泊予約処理 | `app.reservation.ReserveRoomControl` | 部屋を予約する=`reserveRoom` |
| 宿泊部屋検索処理 | `app.search.RoomSearchControl` | 部屋を検索する=`searchRooms` / 部屋の空室を確認する=`checkVacancy` / 空室一覧を作成する=`makeVacantRoomList` |
| 部屋管理処理 | `app.management.RoomManagementControl` | 部屋を追加する=`addRoom` / 全部屋を取得する=`getAllRooms` |
| チェックイン制御 | `app.checkin.CheckInRoomControl` | 予約を検索する=`findReservation` / チェックイン完了を指示する=`completeCheckIn` |
| チェックアウト制御 | `app.checkout.CheckOutRoomControl` | 宿泊データを検索する=`findStay` / チェックアウトを完了する=`completeCheckOut` |

### Entity (ドメイン・データ) → `domain.*`

| クラス図 | Java クラス | 属性・メソッド対応 |
|---|---|---|
| 利用者 | `domain.user.User` | 利用者名=`userName` (予約する → `Reservation.userName`) |
| 予約 | `domain.reservation.Reservation` (+`ReservationManager`) | 予約番号=`reservationNumber` / 予約宿泊予定日=`stayingDate` / 状態=`status` / 予約を作成する=`createReservation` / 予約番号を発行する=`issueReservationNumber` / 予約詳細を取得する=`getReservationDetail` / 状態を「宿泊中」にする=`changeStatusToStaying` |
| 部屋 | `domain.room.Room` (+`RoomManager`) | 部屋番号・部屋タイプ・基本宿泊料・空室状態 / [空室数>0]部屋を検索する=`searchVacantRooms` / 空室数を1減らす=`reserveRoom` / 空室を使用中にする=`markOccupied` / 部屋を空室にする=`markVacant` / 基本宿泊料を取得する=`getBasicRate` |
| 宿泊 | `domain.stay.Stay` (+`StayManager`) | イン日時=`checkInDateTime` / アウト日時=`checkOutDateTime` / 宿泊記録を生成する=`createStay` / 滞在詳細を取得する=`getStayDetail` / アウト日時を記録する=`recordCheckOutDateTime` |
| ホテル受付係 | `domain.user.Receptionist` | 名前=`name` (対応 → `Stay.receptionistName`) |
| 支払い | `domain.payment.Payment` (+`PaymentManager`) | 支払い料金=`amount` / 支払い記録を生成する=`createPaymentRecord` |

### 関連 (アソシエーション) の実装

- 利用者 --予約する--> 予約 : `Reservation` が `userName` を保持
- 予約 --対象--> 部屋 (1) : `Reservation` が `roomNumber` を保持
- 部屋 1 --記録--> 0..1 宿泊 : `StayDao.getStayingStay(部屋番号)` で宿泊中の記録を1件取得
- ホテル受付係 --対応--> 宿泊 : `Stay` が `receptionistName` を保持
- 宿泊 --精算--> 支払い : チェックアウト時に `PaymentManager.createPayment` で生成

## 実行の流れ (例)

1. メニュー「1: 空室検索」 — タイプ指定または全タイプの空室一覧を表示 (空室なしなら警告)
2. メニュー「2: 宿泊予約」 — 利用者名・部屋タイプ・宿泊予定日を入力 → 予約番号を発行・表示 (空室数を1減らす)
3. メニュー「3: チェックイン」 — 予約番号で予約を検索・表示 → 受付係が対応し確定 → 予約が「宿泊中」になり宿泊記録を生成 (イン日時)
4. メニュー「4: チェックアウト」 — 部屋番号で宿泊データを検索 → アウト日時を記録し支払い記録 (基本宿泊料) を生成 → 部屋を空室に戻す
5. メニュー「5: 部屋管理」 — 部屋の追加・全部屋の表示

## 初期データ

| 部屋番号 | タイプ | 基本宿泊料 |
|---|---|---|
| 101, 102, 103 | シングル | 8,000円 |
| 201, 202 | ダブル | 12,000円 |
| 301 | スイート | 30,000円 |
