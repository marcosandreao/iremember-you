/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.simpleapp.rememberyou.contacts.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Contacts.Photo;
import android.provider.ContactsContract.Data;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;

import br.com.simpleapp.rememberyou.BuildConfig;
import br.com.simpleapp.rememberyou.IConstatns;
import br.com.simpleapp.rememberyou.R;
import br.com.simpleapp.rememberyou.contacts.util.ImageLoader;
import br.com.simpleapp.rememberyou.contacts.util.Utils;
import br.com.simpleapp.rememberyou.entity.User;
import br.com.simpleapp.rememberyou.gcm.QuickstartPreferences;
import br.com.simpleapp.rememberyou.service.SendRemember;
import br.com.simpleapp.rememberyou.service.UserService;
import br.com.simpleapp.rememberyou.utils.DialogUtils;
import br.com.simpleapp.rememberyou.utils.Emotions;
import br.com.simpleapp.rememberyou.utils.NotificationUtil;
import br.com.simpleapp.rememberyou.utils.SendState;


public class ContactDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>, NotificationUtil.IPinnedNotificationListener {

    public static final String EXTRA_CONTACT_URI =
            "com.example.android.contactslist.ui.EXTRA_CONTACT_URI";

    // Defines a tag for identifying log entries
    private static final String TAG = "ContactDetailFragment";


    private Uri mContactUri; // Stores the contact Uri for this fragment instance
    private ImageLoader mImageLoader; // Handles loading the contact image in a background thread

    // Used to store references to key views, layouts and menu items as these need to be updated
    // in multiple methods throughout this class.
    private ImageView mImageView;
    private TextView mEmptyView;
    private TextView mContactName;
    private MenuItem mEditContactMenuItem;
    private String emailAddress;
    private TextView mContactEmail;

    private UserService favoriteService = new UserService();
    private String contactName;

    private FloatingActionButton favoriteActionButton;
    private FloatingActionButton fabsend;
    private ImageView ivEmotionTarget;
    private String contactId;

    private View sendProgress;

    private final IntentFilter filter = IConstatns.INTENT_FILTER_DETAIL;

    /**
     * Factory method to generate a new instance of the fragment given a contact Uri. A factory
     * method is preferable to simply using the constructor as it handles creating the bundle and
     * setting the bundle as an argument.
     *
     * @param contactUri The contact Uri to load
     * @return A new instance of {@link ContactDetailFragment}
     */
    public static ContactDetailFragment newInstance(Uri contactUri) {
        // Create new instance of this fragment
        final ContactDetailFragment fragment = new ContactDetailFragment();

        // Create and populate the args bundle
        final Bundle args = new Bundle();
        args.putParcelable(EXTRA_CONTACT_URI, contactUri);

        // Assign the args bundle to the new fragment
        fragment.setArguments(args);

        // Return fragment
        return fragment;
    }

    /**
     * Fragments require an empty constructor.
     */
    public ContactDetailFragment() {}

    /**
     * Sets the contact that this Fragment displays, or clears the display if the contact argument
     * is null. This will re-initialize all the views and start the queries to the system contacts
     * provider to populate the contact information.
     *
     * @param contactLookupUri The contact lookup Uri to load and display in this fragment. Passing
     *                         null is valid and the fragment will display a message that no
     *                         contact is currently selected instead.
     */
    public void setContact(Uri contactLookupUri) {

        this.contactId = contactLookupUri.toString();


        // In version 3.0 and later, stores the provided contact lookup Uri in a class field. This
        // Uri is then used at various points in this class to map to the provided contact.
        if (Utils.hasHoneycomb()) {
            mContactUri = contactLookupUri;
        } else {
            // For versions earlier than Android 3.0, stores a contact Uri that's constructed from
            // contactLookupUri. Later on, the resulting Uri is combined with
            // Contacts.Data.CONTENT_DIRECTORY to map to the provided contact. It's done
            // differently for these earlier versions because Contacts.Data.CONTENT_DIRECTORY works
            // differently for Android versions before 3.0.
            mContactUri = Contacts.lookupContact(getActivity().getContentResolver(),
                    contactLookupUri);
        }


        // If the Uri contains data, load the contact's image and load contact details.
        if (contactLookupUri != null) {
            // Asynchronously loads the contact image
            mImageLoader.loadImage(mContactUri, mImageView);

            // Shows the contact photo ImageView and hides the empty view
            mImageView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);

            // Shows the edit contact action/menu item
            if (mEditContactMenuItem != null) {
                mEditContactMenuItem.setVisible(true);
            }


            Log.d("ID", "id do contato " + this.contactId);
            // Starts two queries to to retrieve contact information from the Contacts Provider.
            // restartLoader() is used instead of initLoader() as this method may be called
            // multiple times.
            getLoaderManager().restartLoader(ContactDetailQuery.QUERY_ID, null, this);
            getLoaderManager().restartLoader(ContactEmailQuery.QUERY_ID, null, this);
        } else {
            // If contactLookupUri is null, then the method was called when no contact was selected
            // in the contacts list. This should only happen in a two-pane layout when the user
            // hasn't yet selected a contact. Don't display an image for the contact, and don't
            // account for the view's space in the layout. Turn on the TextView that appears when
            // the layout is empty, and set the contact name to the empty string. Turn off any menu
            // items that are visible.
            mImageView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
            if (mContactName != null) {
                mContactName.setText("");
            }
            if (mEditContactMenuItem != null) {
                mEditContactMenuItem.setVisible(false);
            }
        }
    }

    /**
     * When the Fragment is first created, this callback is invoked. It initializes some key
     * class fields.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Let this fragment contribute menu items
        setHasOptionsMenu(true);

        /*
         * The ImageLoader takes care of loading and resizing images asynchronously into the
         * ImageView. More thorough sample code demonstrating background image loading as well as
         * details on how it works can be found in the following Android Training class:
         * http://developer.android.com/training/displaying-bitmaps/
         */
        mImageLoader = new ImageLoader(getActivity(), getLargestScreenDimension()) {
            @Override
            protected Bitmap processBitmap(Object data) {
                // This gets called in a background thread and passed the data from
                // ImageLoader.loadImage().
                return loadContactPhoto((Uri) data, getImageSize());

            }
        };

        // Set a placeholder loading image for the image loader
        mImageLoader.setLoadingImage(R.drawable.ic_contact_picture_180_holo_light);

        // Tell the image loader to set the image directly when it's finished loading
        // rather than fading in
        mImageLoader.setImageFadeIn(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Inflates the main layout to be used by this fragment
        final View detailView =
                inflater.inflate(R.layout.contact_detail_fragment, container, false);

        ContactDetailActivity activity =  (ContactDetailActivity) this.getActivity();
        activity.setSupportActionBar((Toolbar) detailView.findViewById(R.id.toolbar));
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Gets handles to view objects in the layout
        mImageView = (ImageView) detailView.findViewById(R.id.contact_image);
        mEmptyView = (TextView) detailView.findViewById(android.R.id.empty);
        mContactEmail = (TextView) detailView.findViewById(R.id.contact_email);
        mContactName = (TextView) detailView.findViewById(R.id.contact_name);
        this.sendProgress = detailView.findViewById(R.id.send_loading);
        this.sendProgress.setVisibility(View.INVISIBLE);
        this.ivEmotionTarget = (ImageView) detailView.findViewById(R.id.ivEmotionTarget);
        this.favoriteActionButton = (FloatingActionButton) detailView.findViewById(R.id.fab);
        this.fabsend = (FloatingActionButton) detailView.findViewById(R.id.fabsend);
        this.favoriteActionButton.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                if( emailAddress != null ) {
                    favoriteService.setWithFavorie(contactId, contactName, emailAddress);
                    setImageFavorite();
                } else {
                    Toast.makeText(ContactDetailFragment.this.getContext(), R.string.loading_contacts, Toast.LENGTH_SHORT).show();
                }
            }
        });
        setClicksEmotions( (ViewGroup) detailView.findViewById(R.id.llEmotions));
        return detailView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

        ((TextView) view.findViewById(R.id.not_name)).setText(sharedPreferences.getString(QuickstartPreferences.NICK_NAME, ""));

        this.fabsend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });

        LocalBroadcastManager.getInstance(this.getActivity()).registerReceiver(this.receiverSend, this.filter);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this.getActivity()).unregisterReceiver(this.receiverSend);
    }

    private void send(){

        if ( fabsend.getTag() != null && fabsend.getTag() instanceof SendState) {

            final SendState state = (SendState) fabsend.getTag();
            if ( state == SendState.STATE_START ) {
                return;
            }
            if ( state == SendState.STATE_DONE_NEED_INVITE ) {
                DialogUtils.showLocationDialogNeedInvite(this.getActivity(), this.contactName, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().onBackPressed();
                    }
                });
                return;
            }
            if ( state == SendState.STATE_DONE_ERROR ) {
                DialogUtils.showLocationDialogError(this.getActivity(), this.contactName, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().onBackPressed();

                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fabsend.setTag(null);
                        send();
                    }
                });
                return;
            }
            this.getActivity().onBackPressed();
            return;
        }
        this.favoriteService.prepareToSent(this.contactId, this.contactName, this.emailAddress, this.ivEmotionTarget.getTag().toString());

        SendRemember.startActionSend(this.getContext(), this.emailAddress, this.ivEmotionTarget.getTag().toString(), this.filter.getAction(0));

        this.fabsend.setTag(SendState.STATE_START);
        this.sendProgress.setVisibility(View.VISIBLE);
    }


    private void setClicksEmotions(ViewGroup view){
        for ( int i = 0; i < view.getChildCount(); i++ ){
            ViewGroup group = (ViewGroup) view.getChildAt(i);
            for ( int j = 0; j < group.getChildCount(); j++ ){
                View viewItem = group.getChildAt(j);
                viewItem.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String tag = v.getTag().toString();
                        mark(tag);

                    }
                });
            }

        }
    }

    private void mark(String tag) {
        ivEmotionTarget.setTag(tag);
        Log.d("tag", tag);
        ivEmotionTarget.setImageResource(Emotions.getByKey(tag));
        markItem();
    }

    private void markItem(){
        ViewGroup view = (ViewGroup) getView().findViewById(R.id.llEmotions);
        for ( int i = 0; i < view.getChildCount(); i++ ){
            ViewGroup group = (ViewGroup) view.getChildAt(i);
            for ( int j = 0; j < group.getChildCount(); j++ ){
                View viewItem = group.getChildAt(j);
                if ( viewItem.getTag().toString().equals(ivEmotionTarget.getTag().toString()) ){
                    viewItem.setAlpha(1F);
                } else {
                    viewItem.setAlpha(0.5F);
                }
            }

        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // If not being created from a previous state
        if (savedInstanceState == null) {
            // Sets the argument extra as the currently displayed contact
            setContact(getArguments() != null ?
                    (Uri) getArguments().getParcelable(EXTRA_CONTACT_URI) : null);
        } else {
            // If being recreated from a saved state, sets the contact from the incoming
            // savedInstanceState Bundle
            setContact((Uri) savedInstanceState.getParcelable(EXTRA_CONTACT_URI));
        }
    }

    /**
     * When the Fragment is being saved in order to change activity state, save the
     * currently-selected contact.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Saves the contact Uri
        outState.putParcelable(EXTRA_CONTACT_URI, mContactUri);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_unpin:

                User user = this.favoriteService.findByEmail(this.emailAddress);
                if (user != null ){
                    NotificationUtil.removeNotification(this.getContext(), user.getId());
                }
                this.getActivity().invalidateOptionsMenu();
                return true;
            case R.id.menu_pin:

                long id = this.favoriteService.prepareToSent(this.contactId, this.contactName, emailAddress, this.ivEmotionTarget.getTag().toString());

                NotificationUtil.pinNotification(this.getActivity(), this.contactId, (int) id,  emailAddress,
                        contactName, this.ivEmotionTarget.getTag().toString(), this);


                this.getActivity().invalidateOptionsMenu();
                return true;
            default:
                this.getActivity().onBackPressed();
                return true;

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.contact_detail_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem itemUnpin = menu.findItem(R.id.menu_unpin);
        MenuItem itemPin = menu.findItem(R.id.menu_pin);
        if ( this.emailAddress != null && !"".equals(this.emailAddress) ) {
            User user = this.favoriteService.findByEmail(this.emailAddress);
            if (user != null && NotificationUtil.hasNotificationPinned(this.getActivity(), user.getId()) ){
                itemUnpin.setVisible(true);
            } else {
                itemUnpin.setVisible(false);
            }
        } else {
            itemUnpin.setVisible(false);
        }

        itemPin.setVisible(!itemUnpin.isVisible());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            // Two main queries to load the required information
            case ContactDetailQuery.QUERY_ID:
                // This query loads main contact details, see
                // ContactDetailQuery for more information.
                return new CursorLoader(getActivity(), mContactUri,
                        ContactDetailQuery.PROJECTION,
                        null, null, null);
            case ContactEmailQuery.QUERY_ID:
                final Uri uri2 = Uri.withAppendedPath(mContactUri, Contacts.Data.CONTENT_DIRECTORY);
                // This query loads contact email details.
                return new CursorLoader(getActivity(), uri2,
                        ContactEmailQuery.PROJECTION,
                        ContactEmailQuery.SELECTION,
                        null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        // If this fragment was cleared while the query was running
        // eg. from from a call like setContact(uri) then don't do
        // anything.
        if (mContactUri == null) {
            return;
        }

        switch (loader.getId()) {
            case ContactDetailQuery.QUERY_ID:
                // Moves to the first row in the Cursor
                if (data.moveToFirst()) {
                    final String contactName = data.getString(ContactDetailQuery.DISPLAY_NAME);

                    if ( contactName != null ) {
                        this.contactName = contactName;
                        mContactName.setText(contactName);
                        getActivity().setTitle(contactName);
                        CollapsingToolbarLayout collapsingToolbar =
                                (CollapsingToolbarLayout) this.getView().findViewById(R.id.collapsing_toolbar);
                        collapsingToolbar.setTitle(contactName);
                    }

                }
                break;

            case ContactEmailQuery.QUERY_ID:
                if (data.moveToFirst()) {
                    do {
                        mContactEmail.setText(data.getString(ContactEmailQuery.ADDRESS));
                        this.emailAddress = data.getString(ContactEmailQuery.ADDRESS);

                        final User user = this.favoriteService.findByEmail(this.emailAddress);
                        if ( user != null && user.getLastEmotion() != null && user.getLastEmotion().length() > 0 ) {
                            mark(user.getLastEmotion());
                        }

                        this.setImageFavorite();
                        this.getActivity().invalidateOptionsMenu();

                        break;
                    } while (data.moveToNext());
                }
                break;
        }
    }

    private void setImageFavorite(){

        if( this.emailAddress != null && this.favoriteService.isFavorie(this.emailAddress) ) {
            this.favoriteActionButton.setImageResource(R.drawable.ic_star_white_18dp);
        } else {
            this.favoriteActionButton.setImageResource(R.drawable.ic_star_border_white_18dp);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Nothing to do here. The Cursor does not need to be released as it was never directly
        // bound to anything (like an adapter).
    }


    /**
     * Fetches the width or height of the screen in pixels, whichever is larger. This is used to
     * set a maximum size limit on the contact photo that is retrieved from the Contacts Provider.
     * This limit prevents the app from trying to decode and load an image that is much larger than
     * the available screen area.
     *
     * @return The largest screen dimension in pixels.
     */
    private int getLargestScreenDimension() {
        // Gets a DisplayMetrics object, which is used to retrieve the display's pixel height and
        // width
        final DisplayMetrics displayMetrics = new DisplayMetrics();

        // Retrieves a displayMetrics object for the device's default display
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;

        // Returns the larger of the two values
        return height > width ? height : width;
    }

    /**
     * Decodes and returns the contact's thumbnail image.
     * @param contactUri The Uri of the contact containing the image.
     * @param imageSize The desired target width and height of the output image in pixels.
     * @return If a thumbnail image exists for the contact, a Bitmap image, otherwise null.
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private Bitmap loadContactPhoto(Uri contactUri, int imageSize) {

        if (!isAdded() || getActivity() == null) {
            return null;
        }

        final ContentResolver contentResolver = getActivity().getContentResolver();

        AssetFileDescriptor afd = null;

        if (Utils.hasICS()) {
            try {
                Uri displayImageUri = Uri.withAppendedPath(contactUri, Photo.DISPLAY_PHOTO);

                afd = contentResolver.openAssetFileDescriptor(displayImageUri, "r");
                if (afd != null) {
                    return ImageLoader.decodeSampledBitmapFromDescriptor(
                            afd.getFileDescriptor(), imageSize, imageSize);
                }
            } catch (FileNotFoundException e) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Contact photo not found for contact " + contactUri.toString()
                            + ": " + e.toString());
                }
            } finally {
                if (afd != null) {
                    try {
                        afd.close();
                    } catch (IOException e) {
                    }
                }
            }
        }

        try {
            Uri imageUri = Uri.withAppendedPath(contactUri, Photo.CONTENT_DIRECTORY);

            afd = getActivity().getContentResolver().openAssetFileDescriptor(imageUri, "r");

            if (afd != null) {
                return ImageLoader.decodeSampledBitmapFromDescriptor(
                        afd.getFileDescriptor(), imageSize, imageSize);
            }
        } catch (FileNotFoundException e) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Contact photo not found for contact " + contactUri.toString()
                        + ": " + e.toString());
            }
        } finally {
            if (afd != null) {
                try {
                    afd.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    @Override
    public void onFinish() {
        if ( this.isAdded() && this.isVisible() ) {
            this.getActivity().invalidateOptionsMenu();
        }
    }



    public interface ContactDetailQuery {
        int QUERY_ID = 1;

        @SuppressLint("InlinedApi")
        String[] PROJECTION = {
                Contacts._ID,
                Utils.hasHoneycomb() ? Contacts.DISPLAY_NAME_PRIMARY : Contacts.DISPLAY_NAME,
        };

        int DISPLAY_NAME = 1;
    }


    public interface ContactEmailQuery {
        int QUERY_ID = 3;

        String[] PROJECTION =
                {
                        ContactsContract.CommonDataKinds.Email._ID,
                        ContactsContract.CommonDataKinds.Email.ADDRESS,
                        ContactsContract.CommonDataKinds.Email.TYPE,
                        ContactsContract.CommonDataKinds.Email.LABEL
                };

        String SELECTION =
                Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE + "'";

        int ADDRESS = 1;
    }

    public BroadcastReceiver receiverSend = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(SendRemember.EXTRA_STATE, -1);
            switch (state){
                case SendRemember.STATE_DONE_ERROR:
                    sendProgress.setVisibility(View.INVISIBLE);
                    fabsend.setImageResource(R.drawable.ic_cloud_off_white_24dp);
                    fabsend.setTag(SendState.STATE_DONE_ERROR);
                    break;
                case SendRemember.STATE_DONE_SUCCESS:
                    sendProgress.setVisibility(View.INVISIBLE);
                    fabsend.setImageResource(R.drawable.ic_cloud_done_white_24dp);
                    fabsend.setTag(SendState.STATE_DONE_SUCCESS);
                    break;
                case SendRemember.STATE_DONE_NEED_INVITE:
                    sendProgress.setVisibility(View.INVISIBLE);
                    fabsend.setImageResource(R.drawable.ic_cloud_done_white_24dp);
                    fabsend.setTag(SendState.STATE_DONE_NEED_INVITE);
                    break;
                case SendRemember.STATE_START:
                    sendProgress.setVisibility(View.VISIBLE);
                    fabsend.setTag(SendState.STATE_START);
                    break;
            }
        }
    };

}
