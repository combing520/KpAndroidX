package cn.cc1w.app.ui.widget.decoration;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

public class DividerItemDecoration extends RecyclerView.ItemDecoration {
   public static final int HORIZONTAL = LinearLayout.HORIZONTAL;
   public static final int VERTICAL = LinearLayout.VERTICAL;

   // 如果不设置，则默认的分割线为 android.R.attr.listDivider 指定的 drawable
   private static final int[] ATTRS = new int[]{ android.R.attr.listDivider };

   private Drawable mDivider;

   /**
    * Current orientation. Either {@link #HORIZONTAL} or {@link #VERTICAL}.
    */
   private int mOrientation;

   private final Rect mBounds = new Rect();

   /**
    * Creates a divider {@link RecyclerView.ItemDecoration} that can be used with a
    *
    * @param context Current context, it will be used to access resources.
    * @param orientation Divider orientation. Should be {@link #HORIZONTAL} or {@link #VERTICAL}.
    */
   public DividerItemDecoration(Context context, int orientation) {
      final TypedArray a = context.obtainStyledAttributes(ATTRS);
      mDivider = a.getDrawable(0);
      a.recycle();
      setOrientation(orientation);
   }

   /**
    * Sets the orientation for this divider. This should be called if
    * {@link RecyclerView.LayoutManager} changes orientation.
    *
    * @param orientation {@link #HORIZONTAL} or {@link #VERTICAL}
    */
   public void setOrientation(int orientation) {
      if (orientation != HORIZONTAL && orientation != VERTICAL) {
         throw new IllegalArgumentException(
                 "Invalid orientation. It should be either HORIZONTAL or VERTICAL");
      }
      mOrientation = orientation;
   }

   /**
    * Sets the {@link Drawable} for this divider.
    *
    * @param drawable Drawable that should be used as a divider.
    */
   public void setDrawable(@NonNull Drawable drawable) {
      if (drawable == null) {
         throw new IllegalArgumentException("Drawable cannot be null.");
      }
      mDivider = drawable;
   }

   @Override
   public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
      if (parent.getLayoutManager() == null) {
         return;
      }
      if (mOrientation == VERTICAL) {
         drawVertical(c, parent);
      } else {
         drawHorizontal(c, parent);
      }
   }

   // 绘制 RecyclerView 为垂直布局时的分割线，此时分割线为水平分割线
   private void drawVertical(Canvas canvas, RecyclerView parent) {
      canvas.save();
      final int left;
      final int right;
      // 需要考虑clipToPadding的boolean值
      if (parent.getClipToPadding()) {
         left = parent.getPaddingLeft();
         right = parent.getWidth() - parent.getPaddingRight();
         canvas.clipRect(left, parent.getPaddingTop(), right,
                 parent.getHeight() - parent.getPaddingBottom());
      } else {
         left = 0;
         right = parent.getWidth();
      }

      final int childCount = parent.getChildCount();
      for (int i = 0; i < childCount; i++) {
         final View child = parent.getChildAt(i);
         parent.getDecoratedBoundsWithMargins(child, mBounds);
         final int bottom = mBounds.bottom + Math.round(ViewCompat.getTranslationY(child));
         final int top = bottom - mDivider.getIntrinsicHeight();
         mDivider.setBounds(left, top, right, bottom);
         mDivider.draw(canvas);
      }
      canvas.restore();
   }

   // 绘制 RecyclerView 为水平布局时的分割线，此时分割线为垂直分割线
   private void drawHorizontal(Canvas canvas, RecyclerView parent) {
      canvas.save();
      final int top;
      final int bottom;
      // 需要考虑clipToPadding的boolean值
      if (parent.getClipToPadding()) {
         top = parent.getPaddingTop();
         bottom = parent.getHeight() - parent.getPaddingBottom();
         canvas.clipRect(parent.getPaddingLeft(), top,
                 parent.getWidth() - parent.getPaddingRight(), bottom);
      } else {
         top = 0;
         bottom = parent.getHeight();
      }

      final int childCount = parent.getChildCount();
      for (int i = 0; i < childCount; i++) {
         final View child = parent.getChildAt(i);
         parent.getLayoutManager().getDecoratedBoundsWithMargins(child, mBounds);
         final int right = mBounds.right + Math.round(ViewCompat.getTranslationX(child));
         final int left = right - mDivider.getIntrinsicWidth();
         mDivider.setBounds(left, top, right, bottom);
         mDivider.draw(canvas);
      }
      canvas.restore();
   }

   @Override
   public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                              RecyclerView.State state) {
      if (mOrientation == VERTICAL) { // 垂直方向的RecyclerView, item 的 bottom 偏移量 = 分割线高度
         outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
      } else { // 水平方向的RecyclerView， item 的 right 偏移量 = 分割线宽度
         outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
      }
   }

}
