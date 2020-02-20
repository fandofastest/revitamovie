package com.revita.hdmovies2020.revita_adapter;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.revita.hdmovies2020.DetailsActivity;
import com.revita.hdmovies2020.R;
import com.revita.hdmovies2020.revita_model.EpiModel;
import com.revita.hdmovies2020.revita_model.SubtitleModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static android.content.Context.DOWNLOAD_SERVICE;

public class DirectorApater extends RecyclerView.Adapter<DirectorApater.OriginalViewHolder> {

    private List<EpiModel> listepi;

    private Context ctx;
    final DirectorApater.OriginalViewHolder[] viewHolderArray = {null};
    private DirectorApater.OnItemClickListener mOnItemClickListener;
    DirectorApater.OriginalViewHolder viewHolder;
    public static List<SubtitleModel> newsub;
    public static String nowtitle;
    int i=0;
    private int seasonNo;
    String DOWNLOAD_DIRECTORY="/DRAMAQU";

    public interface OnItemClickListener {
        void onItemClick(View view, EpiModel obj, int position, OriginalViewHolder holder);
    }

    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public DirectorApater(Context context, List<EpiModel> items,String name, int seasonNo) {
        ArrayList<EpiModel> arrayList=new ArrayList<>();
        for(int i=0;i<items.size();i++){
            if(items.get(i).getSeson().equals(name)){
                arrayList.add(items.get(i));
            }
        }

        items.clear();
        this.listepi = arrayList;
        this.seasonNo = seasonNo;

        ctx = context;
    }


    @Override
    public DirectorApater.OriginalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DirectorApater.OriginalViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_director_name, parent, false);
        vh = new DirectorApater.OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final DirectorApater.OriginalViewHolder holder, final int position) {

        final EpiModel obj = listepi.get(position);


        String currentString = obj.getEpi();

        String[] separated = currentString.split("-");
        String[] separatedepi = separated[1].split(" ");

        holder.name.setText(separated[0]);
        holder.epi.setText(separatedepi[2]);
        holder.name.setText(obj.getEpi());

        Picasso.get().load(obj.getImageUrl()).placeholder(R.drawable.poster_placeholder)
                .into(holder.episodIv);





//        if (seasonNo == 0) {
//            if (position==i){
//                chanColor(viewHolderArray[0],position);
//                ((DetailsActivity)ctx).setMediaUrlForTvSeries(obj.getStreamURL(), obj.getSeson(), obj.getEpi());
//                new DetailsActivity().iniMoviePlayer(obj.getStreamURL(),obj.getServerType(),ctx);
//                holder.name.setTextColor(ctx.getResources().getColor(R.color.colorPrimary));
//                holder.playStatusTv.setText("Playing");
//                holder.playStatusTv.setVisibility(View.VISIBLE);
//                viewHolderArray[0] =holder;
//                i = listepi.size()+listepi.size() + listepi.size();
//
//            }
//        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                newsub=(obj.getListsubtv(position));
                nowtitle=obj.getEpi();


                final CharSequence[] items = {"Play", "Download"};
                final AlertDialog.Builder builder = new AlertDialog.Builder(ctx,R.style.MyDialogTheme);
//                builder.setTitle("Options");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        if (items[item].equals("Download")) {

                            long downloadFileRef = downloadFile(Uri.parse(obj.getStreamURL()), DOWNLOAD_DIRECTORY, obj.getEpi()+".mp4");
                            if (downloadFileRef != 0) {
                                Toast.makeText(ctx,"Starting download",Toast.LENGTH_LONG).show();

                            }else {
                                Toast.makeText(ctx,"File is not available for download",Toast.LENGTH_LONG).show();

                            }




                            return;



                        } else if (items[item].equals("Play")) {

                            ((DetailsActivity)ctx).setMediaUrlForTvSeries(obj.getStreamURL(), obj.getSeson(), obj.getEpi());
                            boolean castSession = ((DetailsActivity)ctx).getCastSession();
                            //Toast.makeText(ctx, "cast:"+castSession, Toast.LENGTH_SHORT).show();
                            if (!castSession) {


                                new DetailsActivity().iniMoviePlayer(obj.getStreamURL(),obj.getServerType(),ctx);


                            } else {
                                ((DetailsActivity)ctx).showQueuePopup(ctx, holder.cardView, ((DetailsActivity)ctx).getMediaInfo());

                            }

                            chanColor(viewHolderArray[0],position);
                            holder.name.setTextColor(ctx.getResources().getColor(R.color.colorPrimary));
                            holder.playStatusTv.setText("Playing");
                            holder.playStatusTv.setVisibility(View.VISIBLE);



                            viewHolderArray[0] =holder;







                        }

                    }
                });
                AlertDialog alert = builder.create();
                alert.show();










            }
        });

    }

    @Override
    public int getItemCount() {
        return listepi.size();
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public TextView name, playStatusTv,epi;
        public MaterialRippleLayout cardView;
        public ImageView episodIv;

        public OriginalViewHolder(View v) {
            super(v);
            epi=v.findViewById(R.id.no);
            name = v.findViewById(R.id.name);
            playStatusTv = v.findViewById(R.id.play_status_tv);
            cardView=v.findViewById(R.id.lyt_parent);
            episodIv=v.findViewById(R.id.image);
        }
    }

    private void chanColor(DirectorApater.OriginalViewHolder holder, int pos){

        if (holder!=null){
            holder.name.setTextColor(ctx.getResources().getColor(R.color.grey_20));
            holder.playStatusTv.setVisibility(View.GONE);
        }
    }



    private long downloadFile(Uri uri, String fileStorageDestinationUri, String fileName) {

        long downloadReference = 0;

        DownloadManager downloadManager = (DownloadManager)ctx.getSystemService(DOWNLOAD_SERVICE);
        try {
            DownloadManager.Request request = new DownloadManager.Request(uri);

            //Setting title of request
            request.setTitle(fileName);

            //Setting description of request
            request.setDescription("Your file is downloading");

            //set notification when download completed
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            //Set the local destination for the downloaded file to a path within the application's external files directory
            request.setDestinationInExternalPublicDir(fileStorageDestinationUri, fileName);

            request.allowScanningByMediaScanner();

            //Enqueue download and save the referenceId
            downloadReference = downloadManager.enqueue(request);
        } catch (IllegalArgumentException e) {
            Toast.makeText(ctx,"Download link is broken or not availale for download",Toast.LENGTH_LONG).show();

            Log.e(TAG, "Line no: 455,Method: downloadFile: Download link is broken");

        }
        return downloadReference;
    }


}