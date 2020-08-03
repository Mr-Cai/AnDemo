package demo.tencent.ad

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.multidex.MultiDexApplication
import com.qq.e.comm.managers.GDTADManager
import com.qq.e.comm.managers.setting.GlobalSetting

@Suppress("unused")
class ProcessApp : MultiDexApplication() {
    override fun onCreate() {
        config(this)
        super.onCreate()
    }

    private fun config(context: Context) {
        try {
            GDTADManager.getInstance().initWith(context, O.appID)
            GlobalSetting.setChannel(1)
            GlobalSetting.setEnableMediationTool(true)
            val packageName = context.packageName
            //Get all activity classes in the AndroidManifest.xml
            val packageInfo = context.packageManager.getPackageInfo(
                packageName, PackageManager.GET_ACTIVITIES or PackageManager.GET_META_DATA
            )
            if (packageInfo.activities != null) {
                for (activity in packageInfo.activities) {
                    val metaData = activity.metaData
                    if (metaData != null && metaData.containsKey("id")
                        && metaData.containsKey("content") && metaData.containsKey("action")
                    ) {
                        Log.e("gdt", activity.name)
                        try {
                            Class.forName(activity.name)
                        } catch (e: ClassNotFoundException) {
                            continue
                        }
                    }
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }
}

