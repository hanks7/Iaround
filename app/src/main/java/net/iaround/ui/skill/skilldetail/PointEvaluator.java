package net.iaround.ui.skill.skilldetail;

import com.nineoldandroids.animation.TypeEvaluator;

/**
 * 作者：zx on 2017/8/17 20:09
 */
class PointEvaluator implements TypeEvaluator<Point> {

    private Point controllPoint;

    public PointEvaluator(Point controllPoint) {
        this.controllPoint = controllPoint;
    }

    @Override
    public Point evaluate(float fraction, Point startPoint, Point endPoint) {
        int x = (int) ((1 - fraction) * (1 - fraction) * startPoint.getX() + 2 * fraction * (1 - fraction) * controllPoint.getX() + fraction * fraction * endPoint.getX());
        int y = (int) ((1 - fraction) * (1 - fraction) * startPoint.getY() + 2 * fraction * (1 - fraction) * controllPoint.getY() + fraction * fraction * endPoint.getY());
        Point point = new Point(x, y);
        return point;
    }
}
