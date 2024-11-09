import 'package:flutter/services.dart';

class SmartCardService {
  static const platform = MethodChannel('com.example.sunmisdk/smart_card');

  // Call the smartCardExchange method on the native side
  Future<Map<String, dynamic>?> smartCardExchange(
      String command, String lc, String indata, String le) async {
    try {
      // Define the arguments to pass to the native side
      final Map<String, dynamic> arguments = {
        'command': command,
        'lc': lc,
        'indata': indata,
        'le': le,
      };

      // Invoke the method on the native side
      final Map<String, dynamic>? result = await platform.invokeMapMethod<String, dynamic>(
        'smartCardExchange',
        arguments,
      );

      return result;
    } on PlatformException catch (e) {
      print("Failed to call smartCardExchange: '${e.message}'.");
      return {'error': e.message};
    }
  }
}
