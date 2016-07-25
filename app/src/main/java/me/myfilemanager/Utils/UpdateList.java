package me.myfilemanager.Utils;

import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;

import me.myfilemanager.Activity.MainActivity;
import me.myfilemanager.Adapter.AdapterDetailedList;
import me.myfilemanager.R;

/**
 * Created by vV on 2015/9/30.
 */
public class UpdateList extends AsyncTask<String, Void, LinkedList<AdapterDetailedList
        .FileDetail>> {

    String exceptionMessage;
    MainActivity activity;


    public UpdateList(MainActivity activity) {
        super();
        this.activity = activity;


    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected LinkedList<AdapterDetailedList.FileDetail> doInBackground(final String... params) {


        final String path = params[0];  //params[0] first parameter

        if (TextUtils.isEmpty(path)) {
            return null;
        }

        File tempFolder = new File(path);
        //TODO: checks permission
        if (tempFolder.isFile()) {
            tempFolder = tempFolder.getParentFile();
        }

        if (tempFolder == null) {
            tempFolder = new File(Environment
                    .getExternalStorageDirectory().getAbsolutePath());
        }

        String[] unopenableExtensions = {"apk", "mp3", "mp4", "png", "jpg", "jpeg"};

        final LinkedList<AdapterDetailedList.FileDetail> fileDetails = new LinkedList<>();
        final LinkedList<AdapterDetailedList.FileDetail> folderDetails = new LinkedList<>();
        MainActivity.currentFolder = tempFolder.getAbsolutePath();

        if (!tempFolder.canRead()) {
            this.cancel(true);

            // pop up a dialog



                /*if (RootFW.connect()) {
                    com.spazedog.lib.rootfw4.utils.File folder = RootFW.getFile(activity
                    .currentFolder);
                    com.spazedog.lib.rootfw4.utils.File.FileStat[] stats = folder.getDetailedList();

                    if (stats != null) {
                        for (com.spazedog.lib.rootfw4.utils.File.FileStat stat : stats) {
                            *//**
             * @return
             *     The file type ('d'=>Directory, 'f'=>File, 'b'=>Block Device,
             *     'c'=>Character Device, 'l'=>Symbolic Link)
             *//*
                            if (stat.type().equals("d")) {
                                folderDetails.add(new AdapterDetailedList.FileDetail(stat.name(),
                                        activity.getString(R.string.folder),
                                        ""));
                            } else if (!FilenameUtils.isExtension(stat.name().toLowerCase(),
                            unopenableExtensions)
                                    && stat.size() <= 20_000 * FileUtils.ONE_KB) {// Java 7
                                    新增数值中使用下划线分割
                                final long fileSize = stat.size();
                                //SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy
                                hh:mm a");
                                //String date = format.format("");
                                fileDetails.add(new AdapterDetailedList.FileDetail(stat.name(),
                                        FileUtils.byteCountToDisplaySize(fileSize), ""));
                            }
                        }
                    }
                }*/
        } else {

            if (!MainActivity.currentFolder.equals("/")) {
                folderDetails.addFirst(new AdapterDetailedList.FileDetail
                        ("..", activity
                                .getString(R
                                        .string
                                        .parent_dir)
                                , ""));
            } else {
                folderDetails.addFirst(new AdapterDetailedList
                        .FileDetail(activity.getString(R.string.home), activity
                        .getString(R.string.folder), ""));
            }

            File[] files = tempFolder.listFiles();// load file list

            if (files != null) {
                Arrays.sort(files, getFileNameComparator());
                for (final File f : files) {
                    if (f.isDirectory()) {
                        folderDetails.add(new AdapterDetailedList.FileDetail(f.getName(),
                                activity.getString(R.string.folder),
                                ""));
                    } else if (f.isFile()  ) {
                        final long fileSize = f.length();
//                        todo local,folder date
                        SimpleDateFormat format = new SimpleDateFormat();
                        String date = format.format(f.lastModified());
                        fileDetails.add(new AdapterDetailedList.FileDetail(f.getName(),
                                FileUtils.byteCountToDisplaySize(fileSize), date));
                    }
                }

            }


            folderDetails.addAll(fileDetails);
        }
        return folderDetails;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPostExecute(final LinkedList<AdapterDetailedList.FileDetail> names) {
        int oldDataSetSize = MainActivity.adapter.fileDetails.size();
        // if (names != null) {
        MainActivity.adapter.fileDetails.clear();
        MainActivity.adapter.notifyDataSetChanged();
        //   MainActivity.adapter.notifyItemRangeRemoved(0, oldDataSetSize);
        MainActivity.adapter.fileDetails.addAll(names);
        MainActivity.adapter.notifyDataSetChanged();
        //   MainActivity.adapter.notifyItemRangeChanged(0, names.size());


        //  }

        activity.invalidateOptionsMenu();
    }

    private Comparator<File> getFileNameComparator() {

        return new Comparator<File>() {


            public int compare(File f1, File f2) {
                String o1 = f1.getName();
                String o2 = f2.getName();
                return o1.compareToIgnoreCase(o2);
            }
        };

    }

}
