package com.evancoding.drawshapes.shapes;

public enum Shape {

    Star(new StarDrawer()),
    Triangle(new TriangleDrawer()),
    Circle(new CircleDrawer()),
    Square(new SquareDrawer());

    Shape(ShapeDrawer drawer) {
        this.drawer = drawer;
    }

    private final ShapeDrawer drawer;

    public ShapeDrawer getDrawer() {
        return drawer;
    }
}