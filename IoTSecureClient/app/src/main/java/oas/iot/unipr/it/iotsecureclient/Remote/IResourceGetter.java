package oas.iot.unipr.it.iotsecureclient.Remote;

/**
 * Created by nico on 30/04/15.
 */
public interface IResourceGetter<T> {

    public void requestResourceFromUrl(String url, IResourceReady<T> listener);

    public interface IResourceReady<T> {

        public void objectReceived(T result);

    }

}