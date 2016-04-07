package first.my.atomationrgbdemo;

import net.atomation.atomationsdk.interfaces.IAtomDevice;
import net.atomation.atomationsdk.interfaces.IAtomGPIO;

import java.util.UUID;


/**
 * Created by ganitstoler on 3/29/16.
 */
public class BasicAtomDevice implements IAtomDevice, IAtomGPIO{

    @Override
    public void onDeviceFound(String s, String s1, int i, byte[] bytes) {

    }

    @Override
    public void onDeviceConnectionStateChanged(String s, int i, int i1) {

    }

    @Override
    public void onScanning(boolean b) {

    }

    @Override
    public void onBatteryValueRead(String s, float v) {

    }

    @Override
    public void onRemoteRssiValueRead(String s, byte b) {

    }

    @Override
    public void onRssiValueRead(String s, int i) {

    }

    @Override
    public void onWriteExecuted(String s, UUID uuid, int i) {

    }

    @Override
    public void onCommandError(String s, UUID uuid, int i) {

    }

    @Override
    public void onCharacteristicRead(String s, UUID uuid, byte[] bytes, int i) {

    }

    @Override
    public void onCharacteristicChanged(String s, UUID uuid, byte[] bytes) {

    }

    @Override
    public void onGPIOValueChanged(String s, byte[] bytes) {

    }

    @Override
    public void onGPIORead(String s, byte[] bytes) {

    }

    @Override
    public void onADCRead(String s, byte[] bytes) {

    }

    @Override
    public void onCommandExecuted(String s, boolean b, int i) {

    }
}
