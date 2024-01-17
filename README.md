# JoyConAPI

JoyConAPI は、Bluetooth 接続した Joy-Con のデータを Java で利用しやすくするライブラリです。

※ 現在、姿勢角 (Orientation)，各速度 (Angle Velocity)，加速度 (Accelerometer) のみサポートしています

## 導入

GitHub の [本レポジトリ](https://github.com/CY21249/JoyConAPI) の [JoyConAPI の Jar アーカイブファイル](https://github.com/CY21249/JoyConAPI/blob/main/JoyConAPI.jar) より Jar ファイルをダウンロードし、任意の Java プロジェクトで参照ライブラリとして利用してください。

例: プロジェクトのディレクトリに lib ディレクトリを作成し、Jar ファイルを格納

その後、本ライブラリを使用したいクラスファイル等でインポート

```java
import joyconapi.joycon.JoyCon;

class JoyConSample {
  public static void main(String[] args) {
    JoyCon joyCon = JoyCon.searchLeft();
  }
}
```

## package 構成

- joyconapi
  - joycon
    - JoyCon
    - JoyConEvent
  - hid
    - HIDDevice
  - util
    - Debugger

## 使用方法

### Joy-Con に接続する

```java


class JoyConAPITest {
  void main() {
    JoyCon joyCon = JoyCon.searchLeft();
    
    if (joyCon == null) {
      System.out.println("Joy-Con (Left) が見つかりませんでした");
      return;
    }
    
    // 続きの処理…
  }
}
```

### Joy-Con の値を取得する

```java
  double orientation_roll = joyCon.getOrientation().x;
  double angleVelocity_y = joyCon.getAngleVelocity().y;
  double accelerometer_z = joyCon.getAccelerometer().z;
```

### Joy-Con からのデータの受信 (InputReport) があった際に実行する

```java
  joyCon.addEventListener(new JoyConEventListener() {
    void onMoveInput(JoyConEvent event) {
      JoyCon target = event.target;
      System.out.println(target.getOrientation().y);
    }
  });
```

## 参照ライブラリ

- [purejavahidapi](https://github.com/nyholku/purejavahidapi)

## 参考文献

- [Nintendo_Switch_Reverse_Engineering](https://github.com/dekuNukem/Nintendo_Switch_Reverse_Engineering)
- [JoyconLib](https://github.com/elgoupil/joyconLib/tree/master)
- [WebHID Demo](https://tomayac.github.io/joy-con-webhid/demo/)
- [JoyConの加速度センサーを取るための設定変更についての話](https://tomayac.github.io/joy-con-webhid/demo/)
