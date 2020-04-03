import 'package:flutter/services.dart';

class Onfido {
  Onfido() {
    _initMethodChannel();
  }

  MethodChannel _platform;

  void _initMethodChannel() =>
      _platform = const MethodChannel('com.example.flutter_onfido/init');

  Future<bool> init() async {
    try {
      return await _platform.invokeMethod('init');
    } on PlatformException catch (e) {
      print('Can not initial Onfido');
      print(e);
    }
    return false;
  }

  Future<void> start() async {
    try {
      await _platform.invokeMethod('start', {
        'mobileToken': 'mobile-token',
        'applicantId': 'fc4be324-8c30-4436-857f-8d4fc262be7d'
      });
    } on PlatformException catch (e) {
      print('Can not open the onfido flow');
      print(e);
    }
  }
}
