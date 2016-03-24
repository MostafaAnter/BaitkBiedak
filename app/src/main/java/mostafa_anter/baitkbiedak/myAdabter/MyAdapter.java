package mostafa_anter.baitkbiedak.myAdabter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mostafa_anter.baitkbiedak.R;
import mostafa_anter.baitkbiedak.activities.DetailsActivity;
import mostafa_anter.baitkbiedak.activities.MainActivity;
import mostafa_anter.baitkbiedak.fragments.DetailsFragment;
import mostafa_anter.baitkbiedak.fragments.ItemsFragment;
import mostafa_anter.baitkbiedak.models.FeedPOJO;
import mostafa_anter.baitkbiedak.utils.SquaredImageView;
import mostafa_anter.baitkbiedak.utils.Utils;


/**
 * Created by mostafa on 11/03/16.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements View.OnClickListener {
    private static final String TAG = "CustomAdapter";
    private static Context mContext;
    private FeedPOJO[] mDataSet;

    // manage enter animate
    private static final int ANIMATED_ITEMS_COUNT = 2; // number of item that animated is 1
    private int lastAnimatedPosition = -1;

    // manage like animations
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);
    private final Map<RecyclerView.ViewHolder, AnimatorSet> likeAnimations = new HashMap<>();
    private final ArrayList<Integer> likedPositions = new ArrayList<>();

    // put control on one item selected
    private int lastCheckedPosition = -1;




    /**
     * Initialize the constructor of the Adapter.
     *
     * @param mDataSet String[] containing the data to populate views to be used by RecyclerView.
     * @param mContext Context hold context
     */
    public MyAdapter(Context mContext, FeedPOJO[] mDataSet) {
        this.mDataSet = mDataSet;
        this.mContext = mContext;
    }

    @Override
    public void onClick(View v) {
        ViewHolder holder = (ViewHolder) v.getTag();
        lastCheckedPosition = holder.getPosition();
        notifyItemRangeChanged(0, mDataSet.length);

        if (!likedPositions.contains(holder.getPosition())) {
            likedPositions.add(holder.getPosition());
            updateHeartButton(holder, true);
        }
        // add to my database
        //addItem(position);

    }

    /**
     * Provide a reference to the type of views (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView mainTitel;
        private final TextView timeStamp;
        private final TextView textStatusMsg;
        private final TextView textUrl;
        private final SquaredImageView imageView;

        private final ImageButton favorite;
        private final ProgressBar mProgress;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getPosition() + " clicked.");
                    if (MainActivity.mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(DetailsFragment.ARG_ITEM_ID, getPosition() + "");
                        DetailsFragment fragment = new DetailsFragment();
                        fragment.setArguments(arguments);
                        ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, DetailsActivity.class);
                        intent.putExtra(DetailsFragment.ARG_ITEM_ID, getPosition());

                        context.startActivity(intent);
                    }
                }
            });
            mainTitel = (TextView) v.findViewById(R.id.main_title);
            timeStamp = (TextView) v.findViewById(R.id.timestamp);
            textStatusMsg = (TextView) v.findViewById(R.id.txtStatusMsg);
            textUrl = (TextView) v.findViewById(R.id.txtUrl);
            imageView = (SquaredImageView) v.findViewById(R.id.feedImage1);
            favorite = (ImageButton) v.findViewById(R.id.favorite_button);
            mProgress = (ProgressBar) v.findViewById(R.id.progressBar);
        }

        public TextView getMainTitel() {
            return mainTitel;
        }

        public TextView getTimeStamp() {
            return timeStamp;
        }

        public TextView getTextStatusMsg() {
            return textStatusMsg;
        }

        public TextView getTextUrl() {
            return textUrl;
        }

        public SquaredImageView getImageView() {
            return imageView;
        }


        public ImageButton getFavorite() {
            return favorite;
        }

        public ProgressBar getProgressBar(){
            return mProgress;
        }

    }

    // Create new views
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_forecast, viewGroup, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");
        // run enter animation
        runEnterAnimation(viewHolder.itemView, position);

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        viewHolder.getMainTitel().setText(mDataSet[position].getTitle());
        viewHolder.getTimeStamp().setText(mDataSet[position].getTimeStamp());

        // Chcek for empty status message
        if (!TextUtils.isEmpty(mDataSet[position].getContent())) {
            viewHolder.getTextStatusMsg().setText(mDataSet[position].getContent());
            if (ItemsFragment.type == 0) {
                viewHolder.getTextStatusMsg().setVisibility(View.GONE);
            }

        } else {
            // status is empty, remove from view
            viewHolder.getTextStatusMsg().setVisibility(View.GONE);
        }

        // Checking for null feed url
        if (mDataSet[position].getLinkAttachedWithContent() != null) {
            viewHolder.getTextUrl().setText(Html.fromHtml("<a href=\"" + mDataSet[position].getLinkAttachedWithContent() + "\">"
                    + mDataSet[position].getLinkAttachedWithContent() + "</a> "));
            // Making url clickable
            viewHolder.getTextUrl().setMovementMethod(LinkMovementMethod.getInstance());
            if (ItemsFragment.type == 0) {
                viewHolder.getTextUrl().setVisibility(View.GONE);
            }

        } else {
            // url is null, remove from the view
            viewHolder.getTextUrl().setVisibility(View.GONE);
        }

        // Feed image
        if (mDataSet[position].getImageUrl() != null) {
            // show progressBar
            viewHolder.getProgressBar().setVisibility(View.VISIBLE);
            // Adapter re-use is automatically detected and the previous download canceled.
            Picasso.with(mContext).load(mDataSet[position].getImageUrl())
                    .placeholder(R.drawable.rectangle)
                    .into(viewHolder.getImageView(), new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            if (viewHolder.getProgressBar() != null) {
                                viewHolder.getProgressBar().setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onError() {
                        }
                    });
        }
        else {
            viewHolder.getImageView().setVisibility(View.GONE);
        }

        //viewHolder.getImageView().setImageBitmap();

        // like button
        viewHolder.getFavorite().setOnClickListener(this);
        viewHolder.getFavorite().setTag(viewHolder);
        if(position == lastCheckedPosition) {
            viewHolder.getFavorite().setImageResource(R.drawable.ic_favorite_24dp);
        }else {
            viewHolder.getFavorite().setImageResource(R.drawable.ic_favorite_outline_24dp);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.length;
    }

    // manage enter animation function
    private void runEnterAnimation(View view, int position) {
        if (position >= ANIMATED_ITEMS_COUNT - 1) {
            return;
        }

        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            view.setTranslationY(Utils.getScreenHeight(mContext));
            view.animate()
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator(3.f))
                    .setDuration(700)
                    .start();
        }
    }

    // manage animate like button
    private void updateHeartButton(final ViewHolder holder, boolean animated) {
        if (animated) {
            if (!likeAnimations.containsKey(holder)) {
                AnimatorSet animatorSet = new AnimatorSet();
                likeAnimations.put(holder, animatorSet);

                ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(holder.getFavorite(), "rotation", 0f, 360f);
                rotationAnim.setDuration(300);
                rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

                ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(holder.getFavorite(), "scaleX", 0.2f, 1f);
                bounceAnimX.setDuration(300);
                bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

                ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(holder.getFavorite(), "scaleY", 0.2f, 1f);
                bounceAnimY.setDuration(300);
                bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);
                bounceAnimY.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        holder.getFavorite().setImageResource(R.drawable.ic_favorite_24dp);
                    }
                });

                animatorSet.play(rotationAnim);
                animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);

                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        resetLikeAnimationState(holder);
                    }
                });

                animatorSet.start();
            }
        } else {
            if (likedPositions.contains(holder.getPosition())) {
                holder.getFavorite().setImageResource(R.drawable.ic_favorite_24dp);
            } else {
                holder.getFavorite().setImageResource(R.drawable.ic_favorite_outline_24dp);
            }
        }
    }

    private void resetLikeAnimationState(ViewHolder holder) {
        likeAnimations.remove(holder);
    }



}
