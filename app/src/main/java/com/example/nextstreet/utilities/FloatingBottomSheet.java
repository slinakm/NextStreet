package com.example.nextstreet.utilities;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.DrawableRes;
import androidx.annotation.MenuRes;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.nextstreet.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


@SuppressWarnings("RestrictedApi")
public class FloatingBottomSheet extends LinearLayoutCompat implements View.OnClickListener,
        View.OnLongClickListener, FloatingAnimator.FloatingAnimatorListener {

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    @MenuRes
    private int mMenuRes;

    @DrawableRes
    private int mItemBackground;


    private RecyclerView mRecyclerView;
    private AppBarLayout mAppBar;
    FloatingActionButton mFab;
    private View mCustomView;
    private Menu mMenu;
    boolean mShowing;
    boolean mAnimating;
    boolean mHandleFabClick;
    private boolean mAutoHide;
    private boolean mShowToast;
    private Toast mToast;
    private ItemClickListener mClickListener;
    private LinearLayoutCompat mMenuLayout;
    private FloatingAnimator mAnimator;
    private List<MorphListener> mMorphListeners;

    private OnClickListener mViewClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!mShowing && mHandleFabClick) {
                show();
            }
        }
    };

    public FloatingToolbar(Context context) {
        this(context, null, 0);
    }

    public FloatingToolbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FloatingToolbar, 0, 0);

        TypedValue outValue = new TypedValue();

        // Set colorAccent as default color
        if (getBackground() == null) {
            getContext().getTheme().resolveAttribute(R.attr.colorAccent, outValue, true);
            setBackgroundResource(outValue.resourceId);
        }

        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground,
                outValue, true);

        mMorphListeners = new ArrayList<>();
        mShowToast = a.getBoolean(R.styleable.FloatingToolbar_floatingToastOnLongClick, true);
        mHandleFabClick = a.getBoolean(R.styleable.FloatingToolbar_floatingHandleFabClick, true);
        mItemBackground = a.getResourceId(R.styleable.FloatingToolbar_floatingItemBackground,
                outValue.resourceId);
        mAutoHide = a.getBoolean(R.styleable.FloatingToolbar_floatingAutoHide, true);
        mMenuRes = a.getResourceId(R.styleable.FloatingToolbar_floatingMenu, 0);

        int customView = a.getResourceId(R.styleable.FloatingToolbar_floatingCustomView, 0);

        if (customView != 0) {
            mCustomView = LayoutInflater.from(context).inflate(customView, this, false);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mAnimator = new FloatingAnimatorImpl(this);
        } else {
            mAnimator = new FloatingAnimatorLollipopImpl(this);
        }

        if (mCustomView != null) {
            addView(mCustomView);
            mAnimator.setContentView(mCustomView);
        }

        // Set elevation to 6dp
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation(getResources().getDimension(R.dimen.floatingtoolbar_start_elevation));
        }

        if (mMenuRes != 0 && customView == 0) {
            createMenuLayout();
            addMenuItems();
            mAnimator.setContentView(mMenuLayout);
        }

        if (!isInEditMode()) {
            setVisibility(View.INVISIBLE);
        }

        a.recycle();

        setOrientation(HORIZONTAL);
        mSnackBarManager = new FloatingSnackBarManager(this);
    }

    public FloatingBottomSheet(Context context) {
        super(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mAnimator.updateDelay();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mAppBar != null) {
            mAppBar.removeOnOffsetChangedListener(mAnimator);
        }
        super.onDetachedFromWindow();
    }

    /**
     * Check if the FloatingToolbar is being shown due a previous FloatingActionButton click
     * or a manual call to {@link #show() show()}
     *
     * @return true if the FloatingToolbar is being shown and the fab is hidden
     */
    public boolean isShowing() {
        return mShowing;
    }

    /**
     * Control whether the FloatingToolbar should handle fab clicks or force manual calls
     * to {@link #show() show}
     *
     * @param handle true if this FloatingToolbar should be shown automatically on fab click
     */
    public void handleFabClick(boolean handle) {
        mHandleFabClick = handle;
        if (mHandleFabClick && mFab != null) {
            mFab.setOnClickListener(mViewClickListener);
        }
    }

    /**
     * Enable or disable auto hide when a menu item is clicked. The default value is true.
     *
     * @param enable true if you want to enable auto hide
     */
    public void enableAutoHide(boolean enable) {
        mAutoHide = enable;
    }

    /**
     * @return true if the FloatingToolbar is being shown automatically
     * by handling FloatingActionButton clicks.
     */
    public boolean isHandlingFabClick() {
        return mHandleFabClick;
    }

    /**
     * Set a ItemClickListener that'll receive item click events from the View built from a Menu.
     *
     * @param listener Listener that'll receive item click events
     */
    public void setClickListener(ItemClickListener listener) {
        mClickListener = listener;
    }

    /**
     * @return The custom view associated to this FloatingToolbar, or null if there's none.
     */
    @Nullable
    public View getCustomView() {
        return mCustomView;
    }

    /**
     * @return The menu associated to this FloatingToolbar, or null if there's none
     */
    @Nullable
    public Menu getMenu() {
        return mMenu;
    }

    /**
     * Place a view inside this FlootingToolbar. It'll be animated automatically.
     *
     * @param view View to be shown inside this FloatingToolbar
     */
    public void setCustomView(View view) {
        removeAllViews();
        mCustomView = view;
        mAnimator.setContentView(mCustomView);
        addView(view);
    }

    /**
     * Set a menu from it's resource id.
     *
     * @param menuRes menu resource to be set
     */
    public void setMenu(@MenuRes int menuRes) {
        mMenu = new MenuBuilder(getContext());
        new SupportMenuInflater(getContext()).inflate(menuRes, mMenu);
        setMenu(mMenu);
    }

    /**
     * Set a menu that'll be used to show a set of options using icons
     *
     * @param menu menu to be set
     */
    public void setMenu(Menu menu) {
        mMenu = menu;
        if (mMenuLayout == null) {
            createMenuLayout();
        }
        mMenuLayout.removeAllViews();
        addMenuItems();
        mAnimator.setContentView(mMenuLayout);
    }

    /**
     * Attach an AppBarLayout to receive expand and collapse events
     * to adjust the FloatingActionButton position correctly
     *
     * @param appBar AppBarLayout to be attached
     */
    public void attachAppBarLayout(AppBarLayout appBar) {
        if (appBar != null) {
            mAppBar = appBar;
            mAppBar.addOnOffsetChangedListener(mAnimator);
            mAnimator.setAppBarLayout(mAppBar);
        }
    }

    public void detachAppBarLayout() {
        if (mAppBar != null) {
            mAppBar.removeOnOffsetChangedListener(mAnimator);
            mAnimator.setAppBarLayout(null);
            mAppBar = null;
        }
    }

    /**
     * Attach a FloatingActionButton that'll be used for the morph animation.
     * <p>It will be hidden after {@link #show() show()} is called
     * and shown after {@link #hide() hide()} is called.
     * </p>
     *
     * @param fab FloatingActionButton to attach
     */
    public void attachFab(FloatingActionButton fab) {
        if (fab == null) {
            return;
        }
        mFab = fab;
        mAnimator.setFab(mFab);
        mAnimator.setFloatingAnimatorListener(this);

        if (mHandleFabClick) {
            mFab.setOnClickListener(mViewClickListener);
        }
    }

    /**
     * Detach the FloatingActionButton from this FloatingToolbar.
     * <p>This will disable the auto morph on click.</p>
     */
    public void detachFab() {
        mAnimator.setFab(null);
        if (mFab != null) {
            mFab.setOnClickListener(null);
            mFab = null;
        }
    }

    /**
     * Attach a RecyclerView to hide this FloatingToolbar automatically when a scroll is detected.
     *
     * @param recyclerView RecyclerView to listen for scroll events
     */
    public void attachRecyclerView(RecyclerView recyclerView) {
        if (recyclerView != null) {
            mRecyclerView = recyclerView;
            mRecyclerView.addOnScrollListener(mScrollListener);
        }
    }

    /**
     * Detach the current RecyclerView to stop hiding automatically this FloatingToolbar
     * when a scroll is detected.
     */
    public void detachRecyclerView() {
        if (mRecyclerView != null) {
            mRecyclerView.removeOnScrollListener(mScrollListener);
            mRecyclerView = null;
        }
    }

    /**
     * Add a morph listener to listen for animation events
     *
     * @param listener MorphListener to be added
     */
    public void addMorphListener(MorphListener listener) {
        if (!mMorphListeners.contains(listener)) {
            mMorphListeners.add(listener);
        }
    }

    /**
     * Remove a morph listener previous added
     *
     * @param listener MorphListener to be removed
     */
    public void removeMorphListener(MorphListener listener) {
        mMorphListeners.remove(listener);
    }

    /**
     * Removes every morph listener previous added
     */
    public void removeMorphListeners() {
        mMorphListeners.clear();
    }

    /**
     * This method will automatically morph the attached FloatingActionButton
     * into this FloatingToolbar.
     *
     * @throws IllegalStateException if there's no FloatingActionButton attached
     */
    public void show() {
        if (mFab == null) {
            throw new IllegalStateException("FloatingActionButton not attached." +
                    "Please, use attachFab(FloatingActionButton fab).");
        }

        if (mAnimating || mShowing) {
            return;
        }

        if (mSnackBarManager.hasSnackBar()) {
            mSnackBarManager.dismissAndShow();
        } else {
            dispatchShow();
        }
    }

    /**
     * This method will automatically morph the FloatingToolbar into the attached FloatingActionButton
     *
     * @throws IllegalStateException if there's no FloatingActionButton attached
     */
    public void hide() {
        if (mFab == null) {
            throw new IllegalStateException("FloatingActionButton not attached." +
                    "Please, use attachFab(FloatingActionButton fab).");
        }

        if (mShowing && !mAnimating) {

            if (mSnackBarManager.hasSnackBar()) {
                mSnackBarManager.dismissAndHide();
            } else {
                dispatchHide();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (!mShowing || mAnimating) {
            return;
        }

        if (mAutoHide) {
            hide();
        }

        if (mClickListener != null) {
            MenuItem item = (MenuItem) v.getTag();
            mClickListener.onItemClick(item);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (!mShowing || mAnimating) {
            return false;
        }

        if (mClickListener != null) {
            MenuItem item = (MenuItem) v.getTag();
            if (mShowToast) {
                if (mToast != null) {
                    mToast.cancel();
                }
                mToast = Toast.makeText(getContext(), item.getTitle(), Toast.LENGTH_SHORT);
                mToast.setGravity(Gravity.BOTTOM, 0, (int) (getHeight() * 1.25f));
                mToast.show();
            }
            mClickListener.onItemLongClick(item);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onAnimationFinished() {
        mAnimating = false;
        if (!mShowing) {
            for (MorphListener morphListener : mMorphListeners) {
                morphListener.onUnmorphEnd();
            }
        } else {
            for (MorphListener morphListener : mMorphListeners) {
                morphListener.onMorphEnd();
            }
        }
    }

    /**
     * Show a snackbar behind the floating toolbar if it's showing
     *
     * @param snackbar Snackbar to be shown
     */
    public void showSnackBar(Snackbar snackbar) {
        mSnackBarManager.showSnackBar(snackbar);
    }

    /**
     * Place the menu items with icons inside a horizontal LinearLayout
     */
    private void addMenuItems() {

        if (mMenu == null) {
            mMenu = new MenuBuilder(getContext());
            new SupportMenuInflater(getContext()).inflate(mMenuRes, mMenu);
        }

        LinearLayoutCompat.LayoutParams layoutParams
                = new LinearLayoutCompat.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT, 1);

        setWeightSum(mMenu.size());

        for (int i = 0; i < mMenu.size(); i++) {
            MenuItem item = mMenu.getItem(i);
            if (item.isVisible()) {
                AppCompatImageButton imageButton = new AppCompatImageButton(getContext());
                //noinspection ResourceType
                imageButton.setId(item.getItemId() == Menu.NONE ? genViewId() : item.getItemId());
                imageButton.setBackgroundResource(mItemBackground);
                imageButton.setImageDrawable(item.getIcon());
                imageButton.setOnClickListener(this);
                imageButton.setOnLongClickListener(this);
                imageButton.setTag(item);
                mMenuLayout.addView(imageButton, layoutParams);
            }
        }
    }

    private void createMenuLayout() {
        mMenuLayout = new LinearLayoutCompat(getContext());

        LayoutParams layoutParams
                = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        mMenuLayout.setId(genViewId());
        addView(mMenuLayout, layoutParams);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mMenuLayout.setPaddingRelative(0, 0, 0, 0);
        } else {
            mMenuLayout.setPadding(0, 0, 0, 0);
        }
    }

    void dispatchShow() {
        mShowing = true;

        if (getWidth() == 0 && getHeight() == 0) {
            setVisibility(View.VISIBLE);
            if (mFab != null) {
                mFab.setVisibility(View.INVISIBLE);
            }
        } else {
            mAnimating = true;
            mAnimator.show();
            for (MorphListener morphListener : mMorphListeners) {
                morphListener.onMorphStart();
            }
        }
    }

    void dispatchHide() {
        mShowing = false;

        if (getWidth() == 0 && getHeight() == 0) {
            setVisibility(View.INVISIBLE);
            if (mFab != null) {
                mFab.setVisibility(View.VISIBLE);
            }
        } else {
            mAnimator.hide();
            mAnimating = true;
            for (MorphListener morphListener : mMorphListeners) {
                morphListener.onUnmorphStart();
            }
        }
    }

    /**
     * Interface to listen to click events on views with MenuItems.
     * <p>
     * Each method only gets called once, even if the user spams multiple clicks
     * </p>
     */
    public interface ItemClickListener {
        void onItemClick(MenuItem item);

        void onItemLongClick(MenuItem item);
    }

    /**
     * Interface to listen to the morph animation
     */
    public interface MorphListener {
        void onMorphEnd();

        void onMorphStart();

        void onUnmorphStart();

        void onUnmorphEnd();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState state = new SavedState(superState);
        state.morphed = mShowing;
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState savedState = (SavedState) state;

        super.onRestoreInstanceState(savedState.getSuperState());

        if (savedState.morphed) {
            mShowing = true;
            ViewCompat.setTranslationZ(this,
                    getResources().getDimension(R.dimen.floatingtoolbar_translationz));
            setVisibility(View.VISIBLE);
            mFab.setVisibility(View.INVISIBLE);
        }
    }

    static class SavedState extends BaseSavedState {

        boolean morphed;

        SavedState(Parcel source) {
            super(source);
            morphed = source.readByte() == 0x01;
        }

        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeByte((byte) (morphed ? 0x01 : 0x00));
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    // Generate unique view ids to save state properly
    private int genViewId() {
        if (Build.VERSION.SDK_INT < 17) {
            for (; ; ) {
                final int result = sNextGeneratedId.get();
                int newValue = result + 1;
                if (newValue > 0x00FFFFFF)
                    newValue = 1;
                if (sNextGeneratedId.compareAndSet(result, newValue)) {
                    return result;
                }
            }
        } else {
            return View.generateViewId();
        }
    }

    static float dpToPixels(Context context, int dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

}