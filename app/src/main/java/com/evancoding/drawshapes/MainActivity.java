package com.evancoding.drawshapes;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import com.evancoding.drawshapes.checkers.CheckerGroup;
import com.evancoding.drawshapes.checkers.ColorChecker;
import com.evancoding.drawshapes.checkers.ShapeChecker;
import com.evancoding.drawshapes.model.DrawModel;
import com.evancoding.drawshapes.shapes.Shape;
import com.evancoding.drawshapes.utils.FileUtil;
import com.evancoding.drawshapes.utils.Painter;
import com.evancoding.drawshapes.view.DrawView;
import com.evancoding.drawshapes.view.DrawingShapeView;


public class MainActivity extends Activity {

    private static Painter painter = new Painter();
    private static DrawModel model;

    private CheckerGroup<ColorChecker> colorCheckerGroup = new CheckerGroup<ColorChecker>();
    private CheckerGroup<ShapeChecker> shapeCheckerGroup = new CheckerGroup<ShapeChecker>();

    private DrawView drawView;

    private DrawerLayout drawerLayout;
    private View toolsLayout;
    private View buttonsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (model == null) {
            model = new DrawModel(getResources().getIntArray(R.array.draw_colors));
        }

        drawView = ((DrawView) findViewById(R.id.drawView));
        drawView.setup(model, painter, this);

        setupDrawer();

        setupButtons();

        float toolsSize = getResources().getDimension(R.dimen.tools_panel_size);
        setupShapes(toolsSize);
        setupColors(toolsSize);

        setupActionBar();
    }

    //==============================================================================================
    // ActionBar & Menu
    //==============================================================================================
    private void setupActionBar() {
        View actionBarView = LayoutInflater.from(this).inflate(R.layout.action_bar, null);
        actionBarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleDrawer(toolsLayout);
            }
        });

        ((DrawingShapeView) actionBarView.findViewById(R.id.drawingShape)).setup(model, painter);

        ActionBar actionBar = getActionBar();
        actionBar.setCustomView(actionBarView);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuTools:
                toggleDrawer(buttonsLayout);
                return true;
            case R.id.menuGallery:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("content://media/internal/images/media"));
                startActivity(intent);
                return true;
            case R.id.menuExit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //==============================================================================================
    // Drawer
    //==============================================================================================
    private void setupDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolsLayout = findViewById(R.id.toolsLayout);
        buttonsLayout = findViewById(R.id.buttonsLayout);

        //-- Block touch through the panels.

        toolsLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        buttonsLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

    }

    private void toggleDrawer(View drawer) {
        if (drawerLayout.isDrawerOpen(drawer)) {
            drawerLayout.closeDrawer(drawer);
        } else {
            drawerLayout.openDrawer(drawer);
        }
    }

    //==============================================================================================
    // Shapes & Colors & Buttons
    //==============================================================================================
    private void setupShapes(float toolsSize) {
        ViewGroup shapeLayout = (ViewGroup) findViewById(R.id.shapeLayout);
        int shapeSize = (int) (toolsSize * 0.8);
        for (Shape shape : model.getShapes()) {
            ShapeChecker shapeChecker = new ShapeChecker(getApplicationContext(), shape, painter);
            shapeChecker.setLayoutParams(new ViewGroup.LayoutParams(shapeSize, shapeSize));
            shapeLayout.addView(shapeChecker);
            shapeCheckerGroup.add(shapeChecker);

            shapeChecker.setColor(model.getColor());
            model.setOnColorChangeListener(shapeChecker);

            if (shape == model.getShape()) {
                shapeCheckerGroup.setCheckedItem(shapeChecker);
            }
        }

        shapeCheckerGroup.setOnChangeListener(new CheckerGroup.OnChangeListener<ShapeChecker>() {
            @Override
            public void onChange(CheckerGroup<ShapeChecker> group) {
                model.setShape(group.getCheckedItem().getShape());
            }
        });
    }

    private void setupColors(float toolsWidth) {
        GridLayout paletteLayout = (GridLayout) findViewById(R.id.paletteLayout);
        int colorSize = (int) (toolsWidth / 2);
        for (int color : model.getColors()) {
            ColorChecker colorChecker = new ColorChecker(getApplicationContext(), color, painter);
            colorChecker.setLayoutParams(new ViewGroup.LayoutParams(colorSize, colorSize));
            paletteLayout.addView(colorChecker);
            colorCheckerGroup.add(colorChecker);

            if (color == model.getColor()) {
                colorCheckerGroup.setCheckedItem(colorChecker);
            }
        }

        colorCheckerGroup.setOnChangeListener(new CheckerGroup.OnChangeListener<ColorChecker>() {
            @Override
            public void onChange(CheckerGroup<ColorChecker> group) {
                model.setColor(group.getCheckedItem().getColor());
            }
        });
    }

    private void setupButtons() {
        findViewById(R.id.saveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileUtil.save(model.getBitmap(), getApplicationContext());
            }
        });

        final Activity activity = this;
        findViewById(R.id.mailButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileUtil.mail(model.getBitmap(), activity);
            }
        });

        findViewById(R.id.resetButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawView.reset();
            }
        });
    }
}
