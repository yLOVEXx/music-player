/*
 * Copyright (c) 2016. André Mion
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package team.fzo.puppas.mini_player.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import team.fzo.puppas.mini_player.R;
import team.fzo.puppas.mini_player.model.Song;
import team.fzo.puppas.mini_player.model.song_model.SongInACG;
import team.fzo.puppas.mini_player.model.song_model.SongInChinese;
import team.fzo.puppas.mini_player.model.song_model.SongInClassical;
import team.fzo.puppas.mini_player.model.song_model.SongInCure;
import team.fzo.puppas.mini_player.model.song_model.SongInJazz;
import team.fzo.puppas.mini_player.model.song_model.SongInLight;
import team.fzo.puppas.mini_player.model.song_model.SongInPop;
import team.fzo.puppas.mini_player.model.song_model.SongInRap;
import team.fzo.puppas.mini_player.model.song_model.SongInRecent;
import team.fzo.puppas.mini_player.model.song_model.SongInStudy;
import team.fzo.puppas.mini_player.service.PlayService;

import org.litepal.LitePal;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MusicContentUtils {
    private static final int SMALL_SIZE = R.dimen.cover_size_mini;
    private static final int BIG_SIZE = R.dimen.cover_size_maxi;

    /*
    存储model的class对象，用于查找数据库
    */
    public static final Class[] SONG_LIST_CLASS = {
            Song.class, SongInRecent.class,SongInStudy.class, SongInCure.class,
            SongInACG.class, SongInChinese.class, SongInClassical.class,
            SongInPop.class, SongInLight.class, SongInJazz.class,
            SongInRap.class
    };

    public static List<Song> gSongList;
    private static final Uri ALBUM_ART_URI = Uri.parse("content://media/external/audio/albumart");

    public static void getContentFromDb(){
        int songListId = PlayService.getSongListId();
        gSongList = LitePal.order("id desc").find(SONG_LIST_CLASS[songListId]);
    }

    /*
     从手机存储中获取歌曲内容并保存在sqlite中
     */
    public static void getContentFromStorage(Context context){
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        if (cursor != null) {
            Song song;
            String name, artist, path;
            long id, albumId, size, duration;
            int isMusic;

            while (cursor.moveToNext()) {
                song = new Song();
                name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                isMusic = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.IS_MUSIC));

                if(LitePal.find(Song.class, song.getSongId()) != null){
                    continue;
                }

                if(isMusic != 0 && duration >= 500 * 60){
                    song.setName(name);
                    song.setArtist(artist);
                    song.setPath(path);
                    song.setDuration(duration);
                    song.setSize(size);
                    song.setSongId(id);
                    song.setAlbumId(albumId);
                    song.setIsMusic(isMusic);

                    song.save();
                }
            }
        }

        assert cursor != null;
        cursor.close();
    }


    //    转换歌曲时间的格式
    public static String formatTime(long time) {
        String ft;
        if (time / 1000 % 60 < 10) {
            ft = time / 1000 / 60 + ":0" + time / 1000 % 60;
        } else {
            ft = time / 1000 / 60 + ":" + time / 1000 % 60;
        }

        return ft;
    }


    public static Bitmap getArtwork(Context context, long songId, long albumId, boolean small){
        if(albumId < 0) {
            if(songId > 0) {
                Bitmap bm = getArtworkFromFile(context, songId, -1, small);
                if(bm != null) {
                    return bm;
                }
            }

            return getDefaultArtwork(context, small);
        }

        ContentResolver res = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(ALBUM_ART_URI, albumId);
        if(uri != null) {
            InputStream in = null;
            try {
                in = res.openInputStream(uri);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 1;
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(in, null, options);
                if(small)
                    options.inSampleSize = calculateSampleSize(options, SMALL_SIZE, SMALL_SIZE);
                else
                    options.inSampleSize = calculateSampleSize(options, BIG_SIZE, BIG_SIZE);

                options.inJustDecodeBounds = false;
                options.inDither = false;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                in = res.openInputStream(uri);
                return BitmapFactory.decodeStream(in, null, options);

            } catch (FileNotFoundException e) {
                Bitmap bm = getArtworkFromFile(context, songId, albumId, small);
                if(bm != null) {
                    if(bm.getConfig() == null) {
                        bm = bm.copy(Bitmap.Config.RGB_565, false);
                    }
                    return bm;
                }

                return getDefaultArtwork(context, small);

            } finally {
                try {
                    if(in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private static Bitmap getArtworkFromFile(Context context, long songId, long albumId, boolean small){
        Bitmap bm = null;
        if(albumId < 0 && songId < 0) {
            throw new IllegalArgumentException("Must specify an album or a song id");
        }
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            FileDescriptor fd = null;
            if(albumId < 0){
                Uri uri = Uri.parse("content://media/external/audio/media/"
                        + songId + "/albumart");
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if(pfd != null) {
                    fd = pfd.getFileDescriptor();
                }
            } else {
                Uri uri = ContentUris.withAppendedId(ALBUM_ART_URI, albumId);
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if(pfd != null) {
                    fd = pfd.getFileDescriptor();
                }
            }
            options.inSampleSize = 1;
            options.inJustDecodeBounds = true;
            // 调用此方法得到options得到图片大小
            BitmapFactory.decodeFileDescriptor(fd, null, options);
            // 调用calculateSampleSize得到图片缩放的比例
            if(small)
                options.inSampleSize = calculateSampleSize(options, SMALL_SIZE, SMALL_SIZE);
            else
                options.inSampleSize = calculateSampleSize(options, BIG_SIZE, BIG_SIZE);

            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            bm = BitmapFactory.decodeFileDescriptor(fd, null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bm;
    }

    //获取默认专辑图片
    public static Bitmap getDefaultArtwork(Context context, boolean small) {
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inPreferredConfig = Bitmap.Config.RGB_565;
        if(small)
            option.inSampleSize = calculateSampleSize(option, SMALL_SIZE, SMALL_SIZE);
        else
            option.inSampleSize = calculateSampleSize(option, BIG_SIZE, BIG_SIZE);

        return BitmapFactory.decodeResource(context.getResources(),
                R.drawable.album_default, option);
    }

    private static int calculateSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int sampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest sampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / sampleSize) > reqHeight
                    && (halfWidth / sampleSize) > reqWidth) {
                sampleSize *= 2;
            }
        }

        return sampleSize;
    }

    public static Bitmap imageScale(Bitmap bitmap, int dst_w, int dst_h) {
        int src_w = bitmap.getWidth();
        int src_h = bitmap.getHeight();
        float scale_w = ((float) dst_w) / src_w;
        float scale_h = ((float) dst_h) / src_h;
        Matrix matrix = new Matrix();
        matrix.postScale(scale_w, scale_h);
        Bitmap dstbmp = Bitmap.createBitmap(bitmap, 0, 0, src_w, src_h, matrix,
                true);
        return dstbmp;
    }

    public static void storeInPlaylist(Song song, int songModelIndex){
        if(song == null)
            return;

        switch (songModelIndex){
            case 1:
                //历史播放最大数量为100
                if(LitePal.count(SongInRecent.class) >= 100){
                    LitePal.delete(SongInRecent.class, 99);
                }

                List<SongInRecent> res = LitePal.where("songId = ?", String.valueOf(song.getSongId()))
                        .find(SongInRecent.class);
                if(!res.isEmpty()){
                    LitePal.deleteAll(SongInRecent.class, "songId = ?", String.valueOf(song.getSongId()));
                }

                SongInRecent songInRecent = new SongInRecent(song);
                songInRecent.save();
                break;

            case 2:
                if(!LitePal.where("songId = ?", String.valueOf(song.getSongId()))
                        .find(SongInStudy.class).isEmpty())
                    break;

                SongInStudy songInStudy = new SongInStudy(song);
                songInStudy.save();
                break;


            case 3:
                if(!LitePal.where("songId = ?", String.valueOf(song.getSongId()))
                        .find(SongInCure.class).isEmpty())
                    break;

                SongInCure songInCure = new SongInCure(song);
                songInCure.save();
                break;

            case 4:
                if(!LitePal.where("songId = ?", String.valueOf(song.getSongId()))
                        .find(SongInACG.class).isEmpty())
                    break;

                SongInACG songInACG = new SongInACG(song);
                songInACG.save();
                break;

            case 5:
                if(!LitePal.where("songId = ?", String.valueOf(song.getSongId()))
                        .find(SongInChinese.class).isEmpty())
                    break;

                SongInChinese songInChinese = new SongInChinese(song);
                songInChinese.save();
                break;

            case 6:
                if(!LitePal.where("songId = ?", String.valueOf(song.getSongId()))
                        .find(SongInClassical.class).isEmpty())
                    break;

                SongInClassical songInClassical = new SongInClassical(song);
                songInClassical.save();
                break;

            case 7:
                if(!LitePal.where("songId = ?", String.valueOf(song.getSongId()))
                        .find(SongInPop.class).isEmpty())
                    break;

                SongInPop songInPop = new SongInPop(song);
                songInPop.save();
                break;

            case 8:
                if(!LitePal.where("songId = ?", String.valueOf(song.getSongId()))
                        .find(SongInLight.class).isEmpty())
                    break;

                SongInLight songInLight = new SongInLight(song);
                songInLight.save();
                break;

            case 9:
                if(!LitePal.where("songId = ?", String.valueOf(song.getSongId()))
                        .find(SongInJazz.class).isEmpty())
                    break;

                SongInJazz songInJazz = new SongInJazz(song);
                songInJazz.save();
                break;

            case 10:
                if(!LitePal.where("songId = ?", String.valueOf(song.getSongId()))
                        .find(SongInRap.class).isEmpty())
                    break;

                SongInRap songInRap = new SongInRap(song);
                songInRap.save();
                break;

            default:
                break;
        }
    }

}
