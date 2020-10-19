# belibrary

## Requisitos
belibrary es compatible con la API de Android 21 (LOLLIPOP) y superior.
## Agregar libreria a tu proyecto
Agregue lo siguiente en su build.gradle raíz.
```gradle
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
Luego agrega la dependencia de la librería.
```gradle
dependencies {
    implementation 'com.github.beAndroid:belibrary:v1.1'
	}
```
## Iniciar el escaneo de Beacon.
```Kotlin
import com.elias.belibrary.MonitoringBeacon

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val monitor = MonitoringBeacon(this,"8cbde5be-b52d-4466-875e-bd6a7c308978",null,true)
        monitor.init()
    }
}
```
MonitoringBeacon( contexto , appId , userId , debug)
- appId : identificador de la aplicación (generalmente entregado por el proveedor) NO puede ser null
- userId : identificador de usuario (generalmente entregado por el proveedor) PUEDE ser null
- debug : True o False para activar distintos logs de la librería (TAG "BeDebug")
