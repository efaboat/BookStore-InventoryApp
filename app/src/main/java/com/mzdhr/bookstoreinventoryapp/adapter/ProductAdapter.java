package com.mzdhr.bookstoreinventoryapp.adapter;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.mzdhr.bookstoreinventoryapp.R;
import com.mzdhr.bookstoreinventoryapp.database.DatabaseContract;
import com.mzdhr.bookstoreinventoryapp.ui.DetailsActivity;

/**
 * Created by mohammad on 12/4/17.
 * Here I extend CursorAdapter not ArrayAdapter.
 * Why -> CursorAdapter is better performance in UI Method in this case.
 * Now this adapter ProductAdapter can use Cursor from Database Provider.
 */

public class ProductAdapter extends CursorAdapter{
    public static final String TAG = ProductAdapter.class.getSimpleName();

    public ProductAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // FindViews
        TextView productNameTextView = view.findViewById(R.id.product_name_text_view);
        TextView productPriceTextView = view.findViewById(R.id.product_price_text_view);
        TextView productQuantityTextView = view.findViewById(R.id.product_quantity_text_view);
        TextView productSellButton = (Button) view.findViewById(R.id.product_sell_button);

        // Get Indexes
        int productDatabaseIDIndex = cursor.getColumnIndex(DatabaseContract.ProductEntry._ID);
        int productNameIndex = cursor.getColumnIndex(DatabaseContract.ProductEntry.COLUMN_PRODUCT_NAME);
        int productPriceIndex = cursor.getColumnIndex(DatabaseContract.ProductEntry.COLUMN_PRODUCT_PRICE);
        int productQuantityIndex = cursor.getColumnIndex(DatabaseContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);

        // Getting Values (final once, so I can use them inside the anonymous class on the onClickListener)
        final int productDatabaseIDValue = cursor.getInt(productDatabaseIDIndex);
        final int productQuantityValue = cursor.getInt(productQuantityIndex);
        int productPrice = cursor.getInt(productPriceIndex);
        String productName = cursor.getString(productNameIndex);

        // Setting Values to views
        productNameTextView.setText(productName);
        productPriceTextView.setText(context.getString(R.string.price_text) + String.valueOf(productPrice) + context.getString(R.string.dollar_sign));
        productQuantityTextView.setText(context.getString(R.string.quantity_text) + String.valueOf(productQuantityValue));

        // Setting Listener onClick to Sell Button
        productSellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove One Quantity
                int newQuantityValue = productQuantityValue;
                if (newQuantityValue > 0) {
                    newQuantityValue--;

                    // Preparing the new Quantity value
                    ContentValues values = new ContentValues();
                    values.put(DatabaseContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, newQuantityValue);

                    // Insert it into database
                    Uri updateSingleProductUri = ContentUris.withAppendedId(DatabaseContract.ProductEntry.CONTENT_URI_PRODUCT, productDatabaseIDValue);
                    int updatedStatus = context.getContentResolver().update(updateSingleProductUri, values, null, null);
                    Log.d(TAG, "onClick: is updated? ---> " + updatedStatus);

                    // Notify the resolver to update the list item UI
                    context.getContentResolver().notifyChange(updateSingleProductUri, null);
                } else {
                    Toast.makeText(context, R.string.no_product_left, Toast.LENGTH_LONG).show();
                }
            }

        });

        // Setting Listener onClick to the item list view
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Getting current product uri, and putting it inside intent data,
                // To send it to DetailsActivity, so it can use it to read detail's it from Database.
                Uri currentProductUri = ContentUris.withAppendedId(DatabaseContract.ProductEntry.CONTENT_URI_PRODUCT, productDatabaseIDValue);
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.setData(currentProductUri);
                context.startActivity(intent);
            }
        });
    }

}
