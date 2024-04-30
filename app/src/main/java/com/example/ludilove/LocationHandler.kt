import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

class LocationHandler(private val context: Context) {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val REQUEST_CODE_LOCATION_PERMISSION = 123
    private var maxRequests: Int = 0

    interface LocationCallback {
        fun onLocationReceived(latitude: Double, longitude: Double)
        fun onLocationFailed()
    }

    fun getCoordinates(callback: LocationCallback) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Проверяем, нужно ли показать модальное окно
            if (maxRequests < 3) {
                showPermissionDialog(callback)
                maxRequests++
            } else {
                // Превышено максимальное количество попыток запроса разрешения
                // Вызываем колбэк с ошибкой
                callback.onLocationFailed()
            }
        } else {
            // Разрешение на доступ к местоположению уже есть, запрашиваем координаты
            retrieveLocation(callback)
        }
    }

    private fun showPermissionDialog(callback: LocationCallback) {
                // Пользователь разрешил доступ к геопозиции
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_CODE_LOCATION_PERMISSION
                )
    }

    private fun retrieveLocation(callback: LocationCallback) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val cancellationTokenSource = CancellationTokenSource()
        val cancellationToken = cancellationTokenSource.token

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Если разрешение не предоставлено, вызываем колбэк с ошибкой
            callback.onLocationFailed()
            return
        }
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancellationToken
        ).addOnSuccessListener { location: android.location.Location? ->
            location?.let {
                // Если координаты получены успешно, вызываем колбэк с координатами
                callback.onLocationReceived(location.latitude, location.longitude)
            } ?: run {
                // Если координаты не были получены, вызываем колбэк с ошибкой
                callback.onLocationFailed()
            }
        }.addOnFailureListener {
            // Если произошла ошибка при получении координат, вызываем колбэк с ошибкой
            callback.onLocationFailed()
        }
    }
}
