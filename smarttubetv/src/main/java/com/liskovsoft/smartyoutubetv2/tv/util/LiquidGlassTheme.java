package com.liskovsoft.smartyoutubetv2.tv.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Outline;
import android.os.Build.VERSION;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.leanback.widget.BaseCardView;
import androidx.leanback.widget.RowHeaderPresenter;
import androidx.leanback.widget.VerticalGridView;
import com.liskovsoft.smartyoutubetv2.tv.R;
import com.liskovsoft.smartyoutubetv2.tv.ui.widgets.complexiconcardview.ComplexImageCardView;

/**
 * Isolated theme logic for the Liquid Glass UI.
 * Keeps modifications to upstream files minimal to avoid merge conflicts on upstream sync.
 */
public final class LiquidGlassTheme {
    private LiquidGlassTheme() {}

    public static boolean isGlassStyle(Context context) {
        if (context == null) {
            return false;
        }
        TypedArray attrs = context.obtainStyledAttributes(new int[]{R.attr.glassStyle});
        boolean glass = attrs.getBoolean(0, false);
        attrs.recycle();
        return glass;
    }

    @RequiresApi(21)
    public static void applyRoundedClip(View view, float radiusPx) {
        if (view == null || VERSION.SDK_INT < 21) {
            return;
        }
        view.setClipToOutline(true);
        view.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View v, Outline outline) {
                outline.setRoundRect(0, 0, v.getWidth(), v.getHeight(), radiusPx);
            }
        });
    }

    // --- VideoCardPresenter hooks ---

    public static int getCardInset(Context context) {
        return isGlassStyle(context)
                ? context.getResources().getDimensionPixelSize(R.dimen.glass_card_inset)
                : 0;
    }

    public static void applyVideoCardStyle(ComplexImageCardView cardView, int inset) {
        Context context = cardView.getContext();
        float imageRadius = context.getResources().getDimension(R.dimen.glass_card_image_corner_radius);
        cardView.setPadding(inset, inset, inset, inset);
        cardView.setBackgroundResource(R.drawable.bg_glass_card);
        applyRoundedClip(cardView.findViewById(R.id.main_image_wrapper), imageRadius);
    }

    public static void updateVideoCardRim(ComplexImageCardView view, boolean selected) {
        view.setForeground(ContextCompat.getDrawable(view.getContext(),
                selected ? R.drawable.glass_card_rim_focus : R.drawable.glass_card_rim_idle));
    }

    // --- ChannelCardPresenter hooks ---

    public static void setupChannelCard(Context context, View container, TextView textView) {
        int inset = context.getResources().getDimensionPixelSize(R.dimen.glass_card_inset);
        View wrapper = container.findViewById(R.id.channel_card_wrapper);
        if (wrapper != null) {
            wrapper.setPadding(inset, inset, inset, inset);
        }
        container.setBackgroundResource(R.drawable.bg_glass_card_idle);
        applyRoundedClip(container.findViewById(R.id.channel_image),
                context.getResources().getDimension(R.dimen.glass_card_image_corner_radius));
        if (textView != null) {
            textView.setBackground(null);
            textView.setTextColor(ContextCompat.getColor(context, R.color.glass_text_primary));
        }
    }

    public static void updateChannelCardBackground(View container, boolean hasFocus, boolean hasNewContent) {
        container.setBackgroundResource(hasFocus ? R.drawable.bg_glass_card_focus :
                hasNewContent ? R.drawable.bg_glass_card_new : R.drawable.bg_glass_card_idle);
    }

    // --- SettingsCardPresenter hooks ---

    public static void setupSettingsCard(Context context, View container, TextView textView) {
        int inset = context.getResources().getDimensionPixelSize(R.dimen.glass_card_inset);
        container.setPadding(inset, inset, inset, inset);
        container.setBackgroundResource(R.drawable.bg_glass_card_idle);
        if (textView != null) {
            ViewGroup.LayoutParams params = textView.getLayoutParams();
            if (params != null) {
                params.width = context.getResources().getDimensionPixelSize(R.dimen.settings_card_width) - inset * 2;
                textView.setLayoutParams(params);
            }
            textView.setBackground(null);
            textView.setTextColor(ContextCompat.getColor(context, R.color.glass_text_primary));
        }
    }

    public static void updateSettingsCardBackground(View container, boolean hasFocus) {
        container.setBackgroundResource(hasFocus ? R.drawable.bg_glass_card_focus : R.drawable.bg_glass_card_idle);
    }

    // --- IconHeaderItemPresenter hooks ---

    public static void setupSidebarHeader(Context context, View view) {
        view.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_glass_sidebar_pill));
        int padH = (int) (14 * context.getResources().getDisplayMetrics().density);
        int padV = (int) (8 * context.getResources().getDisplayMetrics().density);
        view.setPadding(padH, padV, padH, padV);
        view.setAlpha(1.0f);
    }

    public static void updateSidebarHeaderAlpha(RowHeaderPresenter.ViewHolder holder) {
        float level = holder.getSelectLevel();
        holder.view.setAlpha(1.0f);
        View icon = holder.view.findViewById(R.id.header_icon);
        View label = holder.view.findViewById(R.id.header_label);
        float contentAlpha = 0.65f + level * 0.35f;
        if (icon != null) icon.setAlpha(contentAlpha);
        if (label != null) label.setAlpha(contentAlpha);
    }

    // --- ExtendedHeadersSupportFragment hooks ---

    public static void setupSidebarDock(Context context, View rootView, VerticalGridView listView) {
        if (listView != null) {
            listView.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_glass_sidebar_dock));
            int pad = (int) (10 * context.getResources().getDisplayMetrics().density);
            listView.setPadding(pad, listView.getPaddingTop(), pad, listView.getPaddingBottom());
        }
        if (rootView != null) {
            View fade = rootView.findViewById(R.id.fade_out_edge);
            if (fade != null) {
                fade.setVisibility(View.GONE);
            }
        }
    }

    // --- Dialogs ---

    public static void applyDialogStyle(Context context, View rootView) {
        if (context == null || rootView == null) {
            return;
        }

        View mainContainer = rootView.findViewById(R.id.settings_preference_fragment_container);
        if (mainContainer instanceof FrameLayout && VERSION.SDK_INT >= 21) {
            mainContainer.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        }

        View mainFrame = rootView.findViewById(R.id.main_frame);
        if (mainFrame != null) {
            mainFrame.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_glass_dialog_panel));
        }

        View title = rootView.findViewById(R.id.decor_title_container);
        if (title != null) {
            title.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
            if (VERSION.SDK_INT >= 21) {
                title.setElevation(0f);
            }
        }

        View itemsContainer = rootView.findViewById(R.id.list);
        if (itemsContainer instanceof VerticalGridView) {
            VerticalGridView grid = (VerticalGridView) itemsContainer;
            int padH = (int) (12 * context.getResources().getDisplayMetrics().density);
            grid.setPadding(padH, grid.getPaddingTop(), padH, grid.getPaddingBottom());
            grid.setOnChildLaidOutListener(
                    (parent, child, position, id) -> child.setBackgroundResource(R.drawable.bg_glass_dialog_item)
            );
        }
    }

    // --- Player dock ---

    public static void setupPlayerTransportRow(View transportRow) {
        if (transportRow == null) return;
        Context ctx = transportRow.getContext();
        transportRow.setBackground(ContextCompat.getDrawable(ctx, R.drawable.bg_glass_player_dock));
        int padH = (int) (18 * ctx.getResources().getDisplayMetrics().density);
        int padV = (int) (10 * ctx.getResources().getDisplayMetrics().density);
        transportRow.setPadding(padH, padV, padH, padV);
    }
}
