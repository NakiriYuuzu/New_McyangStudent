package tw.edu.studentmcyang.yuuzu_lib

import android.content.Context
import org.altbeacon.beacon.*

class BeaconController(
    ctx: Context
) {
    companion object {
        private const val TAG = "RangingActivity"
        private const val DEFAULT_FOREGROUND_SCAN_PERIOD = 1000L
    }

    private var beacon: Beacon? = null
    private var beaconManager: BeaconManager
    private var beaconTransmitter: BeaconTransmitter? = null
    private var region: Region
    private var beaconParser: BeaconParser? = null

    init {
        beaconManager = BeaconManager.getInstanceForApplication(ctx)
        beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"))
        beaconManager.foregroundScanPeriod = DEFAULT_FOREGROUND_SCAN_PERIOD

        beaconParser = BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25")

        region = Region("ranged region", null, null, null)
    }

    fun startScanning(beaconModify: BeaconModify) {
        beaconManager.removeAllRangeNotifiers()
        beaconManager.addRangeNotifier(beaconModify::modifyData)
        beaconManager.startRangingBeacons(region)
    }

//    fun beaconInit(url: String?) {
//        beaconManager = BeaconManager.getInstanceForApplication(ctx)
//        region = Region("UniqueID", Identifier.parse(url), null, null)
//        //beacon AddStone m:0-3=4c000215 or alt beacon = m:2-3=0215
//        beaconManager!!.beaconParsers.add(beaconParser)
//        beaconManager!!.foregroundScanPeriod = DEFAULT_FOREGROUND_SCAN_PERIOD
//    }
//
//    fun startScanning(beaconModify: BeaconModify) {
//        beaconManager!!.addRangeNotifier { beacons: Collection<Beacon?>?, region: Region? ->
//            beaconModify.modifyData(
//                beacons,
//                region
//            )
//        }
//        beaconManager!!.startRangingBeacons(region!!)
//    }
//
//    fun stopScanning() {
//        beaconManager!!.removeAllMonitorNotifiers()
//        beaconManager!!.stopRangingBeacons(region!!)
//        beaconManager!!.removeAllRangeNotifiers()
//    }
//
//    fun init_Sign_BroadcastBeacon() {
//        try {
//            if (shareData.major != null && shareData.minor != null) {
//                Log.e(TAG, "Major: " + shareData.major + " Minor: " + shareData.minor)
//                beacon = Beacon.Builder()
//                    .setId1(DefaultSetting.BEACON_UUID_SIGN)
//                    .setId2(shareData.major)
//                    .setId3(shareData.minor)
//                    .setManufacturer(0x0118)
//                    .setTxPower(-79)
//                    .setDataFields(listOf(0L))
//                    .build()
//                beaconTransmitter = BeaconTransmitter(activity, beaconParser)
//                beaconTransmitter!!.advertiseTxPowerLevel =
//                    AdvertiseSettings.ADVERTISE_TX_POWER_HIGH
//                beaconTransmitter!!.advertiseMode = AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY
//            } else {
//                if (shareData.major == null) Toast.makeText(
//                    activity,
//                    "請先點名后在開始廣播！",
//                    Toast.LENGTH_SHORT
//                ).show()
//                if (shareData.question_ID == null) Toast.makeText(
//                    activity,
//                    "無法取得題目ID！",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        } catch (e: Exception) {
//            if (shareData.major == null) Toast.makeText(activity, "請先點名后在開始廣播！", Toast.LENGTH_SHORT)
//                .show()
//            if (shareData.question_ID == null) Toast.makeText(
//                activity,
//                "無法取得題目ID！",
//                Toast.LENGTH_SHORT
//            ).show()
//        }
//    }
//
//    fun init_Race_BroadcastBeacon() {
//        try {
//            if (shareData.major != null && shareData.raceID != null) {
//                Log.e(TAG, "Major: " + shareData.major + " Minor: " + shareData.raceID)
//                beacon = Beacon.Builder()
//                    .setId1(DefaultSetting.BEACON_UUID_RACE)
//                    .setId2(shareData.major)
//                    .setId3(shareData.raceID)
//                    .setManufacturer(0x0118)
//                    .setTxPower(-79)
//                    .setDataFields(listOf(0L))
//                    .build()
//                beaconTransmitter = BeaconTransmitter(activity, beaconParser)
//                beaconTransmitter!!.advertiseTxPowerLevel =
//                    AdvertiseSettings.ADVERTISE_TX_POWER_HIGH
//                beaconTransmitter!!.advertiseMode = AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY
//            } else {
//                if (shareData.major == null) Toast.makeText(
//                    activity,
//                    "請先點名后在開始廣播！",
//                    Toast.LENGTH_SHORT
//                ).show()
//                if (shareData.question_ID == null) Toast.makeText(
//                    activity,
//                    "無法取得題目ID！",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        } catch (e: Exception) {
//            if (shareData.major == null) Toast.makeText(activity, "請先點名后在開始廣播！", Toast.LENGTH_SHORT)
//                .show()
//            if (shareData.question_ID == null) Toast.makeText(
//                activity,
//                "無法取得題目ID！",
//                Toast.LENGTH_SHORT
//            ).show()
//        }
//    }
//
//    fun init_Answer_BroadcastBeacon() {
//        try {
//            if (shareData.major != null && shareData.question_ID != null) {
//                Log.e(TAG, "Major: " + shareData.major + " Minor: " + shareData.raceID)
//                beacon = Beacon.Builder()
//                    .setId1(DefaultSetting.BEACON_UUID_ANSWER)
//                    .setId2(shareData.major)
//                    .setId3(shareData.question_ID)
//                    .setManufacturer(0x0118)
//                    .setTxPower(-69)
//                    .setDataFields(listOf(0L))
//                    .build()
//                beaconTransmitter = BeaconTransmitter(activity, beaconParser)
//                beaconTransmitter!!.advertiseTxPowerLevel =
//                    AdvertiseSettings.ADVERTISE_TX_POWER_HIGH
//                beaconTransmitter!!.advertiseMode = AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY
//            } else {
//                if (shareData.major == null) Toast.makeText(
//                    activity,
//                    "請先點名后在開始廣播！",
//                    Toast.LENGTH_SHORT
//                ).show()
//                if (shareData.question_ID == null) Toast.makeText(
//                    activity,
//                    "無法取得題目ID！",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        } catch (e: Exception) {
//            Toast.makeText(activity, "無法打開beacon，請先點名后在操作！", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    fun init_GroupSecond_BroadcastBeacon() {
//        try {
//            if (shareData.major != null && shareData.desc_ID != null) {
//                Log.e(TAG, "Major: " + shareData.major + " Minor: " + shareData.desc_ID)
//                beacon = Beacon.Builder()
//                    .setId1(DefaultSetting.BEACON_UUID_GROUP)
//                    .setId2(shareData.major)
//                    .setId3(shareData.desc_ID)
//                    .setManufacturer(0x0118)
//                    .setTxPower(-79)
//                    .setDataFields(listOf(0L))
//                    .build()
//                beaconTransmitter = BeaconTransmitter(activity, beaconParser)
//                beaconTransmitter!!.advertiseTxPowerLevel =
//                    AdvertiseSettings.ADVERTISE_TX_POWER_HIGH
//                beaconTransmitter!!.advertiseMode = AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY
//            } else {
//                if (shareData.major == null) Toast.makeText(
//                    activity,
//                    "請先點名后在開始廣播！",
//                    Toast.LENGTH_SHORT
//                ).show()
//                if (shareData.question_ID == null) Toast.makeText(
//                    activity,
//                    "無法取得群組ID！",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        } catch (e: Exception) {
//            Toast.makeText(activity, "無法打開beacon，請先點名后在操作！", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    fun init_GroupThird_BroadcastBeacon() {
//        try {
//            if (shareData.major != null && shareData.desc_ID != null) {
//                Log.e(TAG, "Major: " + shareData.major + " Minor: " + shareData.desc_ID)
//                beacon = Beacon.Builder()
//                    .setId1(DefaultSetting.BEACON_UUID_TEAM)
//                    .setId2(shareData.major)
//                    .setId3(shareData.desc_ID)
//                    .setManufacturer(0x0118)
//                    .setTxPower(-79)
//                    .setDataFields(listOf(0L))
//                    .build()
//                beaconTransmitter = BeaconTransmitter(activity, beaconParser)
//                beaconTransmitter!!.advertiseTxPowerLevel =
//                    AdvertiseSettings.ADVERTISE_TX_POWER_HIGH
//                beaconTransmitter!!.advertiseMode = AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY
//            } else {
//                if (shareData.major == null) Toast.makeText(
//                    activity,
//                    "請先點名后在開始廣播！",
//                    Toast.LENGTH_SHORT
//                ).show()
//                if (shareData.question_ID == null) Toast.makeText(
//                    activity,
//                    "無法取得群組ID！",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        } catch (e: Exception) {
//            Toast.makeText(activity, "無法打開beacon，請先點名后在操作！", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    fun start_BroadcastBeacon() {
//        try {
//            beaconTransmitter!!.startAdvertising(beacon)
//        } catch (e: Exception) {
//            if (shareData.major == null) Toast.makeText(activity, "請先點名后在開始廣播！", Toast.LENGTH_SHORT)
//                .show()
//            if (shareData.question_ID == null) Toast.makeText(
//                activity,
//                "無法取得題目ID！",
//                Toast.LENGTH_SHORT
//            ).show()
//        }
//    }
//
//    fun stop_BroadcastBeacon() {
//        try {
//            beaconTransmitter!!.stopAdvertising()
//        } catch (e: Exception) {
//            if (shareData.major == null) Toast.makeText(activity, "請先點名后在開始廣播！", Toast.LENGTH_SHORT)
//                .show()
//            if (shareData.question_ID == null) Toast.makeText(
//                activity,
//                "無法取得題目ID！",
//                Toast.LENGTH_SHORT
//            ).show()
//        }
//    }

    interface BeaconModify {
        fun modifyData(beacons: Collection<Beacon?>?, region: Region?)
    }
}