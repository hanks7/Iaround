package cn.finalteam.galleryfinal.media;

/**
 * Created by Jaber on 16/8/3.
 */
public interface IFlyMediaCallback {
    public void engineStart(Object sender);
    public void engineStop(Object sender);
    public void enginePause(Object sender);
    public void engineResume(Object sender);
    public void engineError(Object sender, int errCode, String errString);
}
