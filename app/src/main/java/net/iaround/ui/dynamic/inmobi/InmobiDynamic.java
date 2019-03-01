package net.iaround.ui.dynamic.inmobi;

/**
 * @author：liush on 2017/2/20 21:23
 */
public class InmobiDynamic {

    private String title;
    private String description;
    private Icon icon;
    private String landingURL;
    private Screenshots screenshots;
    private String cta;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public String getLandingURL() {
        return landingURL;
    }

    public void setLandingURL(String landingURL) {
        this.landingURL = landingURL;
    }

    public Screenshots getScreenshots() {
        return screenshots;
    }

    public void setScreenshots(Screenshots screenshots) {
        this.screenshots = screenshots;
    }

    public String getCta() {
        return cta;
    }

    public void setCta(String cta) {
        this.cta = cta;
    }

    public class Screenshots{
        private int width;
        private int height;
        private String url;
        private double aspectRatio;

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public double getAspectRatio() {
            return aspectRatio;
        }

        public void setAspectRatio(double aspectRatio) {
            this.aspectRatio = aspectRatio;
        }
    }

    public class Icon{
        private int width;
        private int height;
        private String url;
        private int aspectRatio;

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getAspectRatio() {
            return aspectRatio;
        }

        public void setAspectRatio(int aspectRatio) {
            this.aspectRatio = aspectRatio;
        }
    }
}
