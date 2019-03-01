package net.iaround.ui.datamodel;

import net.iaround.model.im.BaseServerBean;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Created by admin on 2017/5/9.
 */

public class Photos extends BaseServerBean {


    private List<PhotosBean> photos;

    public List<PhotosBean> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PhotosBean> photos) {
        this.photos = photos;
    }

    public static class PhotosBean implements Serializable {
        private static final long serialVersionUID = -3440061414071692254L;
        /**
         * image : http://p3.iaround.com/201610/21/PHOTO/01f74a0687fc57a570ebd9889d978e72.png
         * photoid : null
         * ishdimage : 1
         */

        private String image;
        private Object photoid;
        private int ishdimage;

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public Object getPhotoid() {
            return photoid;
        }

        public void setPhotoid(Object photoid) {
            this.photoid = photoid;
        }

        public int getIshdimage() {
            return ishdimage;
        }

        public void setIshdimage(int ishdimage) {
            this.ishdimage = ishdimage;
        }
    }
}
