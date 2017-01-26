package rkr.binatestation.pathrakkaran.modules.products;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.os.ResultReceiver;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import rkr.binatestation.pathrakkaran.R;
import rkr.binatestation.pathrakkaran.database.DatabaseOperationService;
import rkr.binatestation.pathrakkaran.database.PathrakkaranContract.AgentProductListTable;
import rkr.binatestation.pathrakkaran.database.PathrakkaranContract.CompanyMasterTable;
import rkr.binatestation.pathrakkaran.database.PathrakkaranContract.ProductMasterTable;
import rkr.binatestation.pathrakkaran.fragments.dialogs.AlertDialogFragment;
import rkr.binatestation.pathrakkaran.models.AgentProductModel;
import rkr.binatestation.pathrakkaran.models.CompanyMasterModel;
import rkr.binatestation.pathrakkaran.models.ProductMasterModel;

import static rkr.binatestation.pathrakkaran.utils.Constants.CURSOR_LOADER_LOAD_COMPANIES;
import static rkr.binatestation.pathrakkaran.utils.Constants.CURSOR_LOADER_LOAD_PRODUCTS;
import static rkr.binatestation.pathrakkaran.utils.Constants.KEY_USER_ID;

/**
 * Dialog fragment for adding product for Agent
 */
public class AddProductFragment extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
    private static final String TAG = "AddProductFragment";

    private Map<Long, List<ProductMasterModel>> mProductModelMap = new LinkedHashMap<>();

    private Spinner mCompanySpinner;
    private Spinner mProductsSpinner;
    private ContentLoadingProgressBar mProgressBar;
    private AddProductListener mAddProductListener;
    private long userId;

    public AddProductFragment() {
        // Required empty public constructor
    }

    public static AddProductFragment newInstance(long userId, AddProductListener addProductListener) {
        Log.d(TAG, "newInstance() called");
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_USER_ID, userId);
        AddProductFragment fragment = new AddProductFragment();
        fragment.setArguments(bundle);
        fragment.mAddProductListener = addProductListener;
        Log.d(TAG, "newInstance() returned: " + fragment);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = getArguments().getLong(KEY_USER_ID, 0);
        loadMasters();
    }

    private void loadMasters() {
        LoaderManager loaderManager = getLoaderManager();
        if (loaderManager.getLoader(CURSOR_LOADER_LOAD_COMPANIES) == null) {
            loaderManager.initLoader(CURSOR_LOADER_LOAD_COMPANIES, null, this);
        } else {
            loaderManager.restartLoader(CURSOR_LOADER_LOAD_COMPANIES, null, this);
        }
        if (loaderManager.getLoader(CURSOR_LOADER_LOAD_PRODUCTS) == null) {
            loaderManager.initLoader(CURSOR_LOADER_LOAD_PRODUCTS, null, this);
        } else {
            loaderManager.restartLoader(CURSOR_LOADER_LOAD_PRODUCTS, null, this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_product, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCompanySpinner = (Spinner) view.findViewById(R.id.FAP_select_company);
        mProductsSpinner = (Spinner) view.findViewById(R.id.FAP_select_product);
        FloatingActionButton actionAddProductButton = (FloatingActionButton) view.findViewById(R.id.FAP_action_add_product);
        mProgressBar = (ContentLoadingProgressBar) view.findViewById(R.id.FAP_progress_bar);

        //Set Company data
        setCompanySpinner(null);
        //Set Product Spinner
        setProductsSpinner(null);

        //Set actionAddProduct
        actionAddProductButton.setOnClickListener(this);

        mCompanySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CompanyMasterModel companyMasterModel = (CompanyMasterModel) parent.getAdapter().getItem(position);
                if (companyMasterModel != null) {
                    if (position == 0) {
                        setProductsSpinner(null);
                    } else {
                        setProductsSpinner(mProductModelMap.get(companyMasterModel.getCompanyId()));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setCompanySpinner(List<CompanyMasterModel> companyMasterModelList) {
        if (companyMasterModelList == null) {
            companyMasterModelList = new ArrayList<>();
            companyMasterModelList.add(CompanyMasterModel.getDefaultValue());
        }
        ArrayAdapter companyMasterModelArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, companyMasterModelList);
        companyMasterModelArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (mCompanySpinner != null) {
            mCompanySpinner.setAdapter(companyMasterModelArrayAdapter);
        }
    }

    private void setProductsSpinner(List<ProductMasterModel> productMasterModelList) {
        Log.d(TAG, "setProductsSpinner() called with: productMasterModelList = [" + productMasterModelList + "]");
        if (productMasterModelList == null) {
            productMasterModelList = new ArrayList<>();
            productMasterModelList.add(ProductMasterModel.getDefaultValue());
        }
        ArrayAdapter productMasterModelArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, productMasterModelList);
        productMasterModelArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (mProductsSpinner != null) {
            mProductsSpinner.setAdapter(productMasterModelArrayAdapter);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case CURSOR_LOADER_LOAD_COMPANIES:
                return new CursorLoader(
                        getContext(),
                        CompanyMasterTable.CONTENT_URI,
                        null,
                        CompanyMasterTable.COLUMN_COMPANY_STATUS + " = ? ",
                        new String[]{"1"},
                        null
                );
            case CURSOR_LOADER_LOAD_PRODUCTS:
                return new CursorLoader(
                        getContext(),
                        ProductMasterTable.CONTENT_URI,
                        null,
                        ProductMasterTable.COLUMN_PRODUCT_STATUS + " = ? and " +
                                ProductMasterTable.COLUMN_PRODUCT_ID + " not in ( select " + AgentProductListTable.COLUMN_PRODUCT_ID + " from " + AgentProductListTable.TABLE_NAME + " " +
                                "where " + AgentProductListTable.COLUMN_AGENT_ID + " = ? )",
                        new String[]{"1", "" + userId},
                        null
                );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished() called with: loader = [" + loader + "], data = [" + data + "]");
        switch (loader.getId()) {
            case CURSOR_LOADER_LOAD_COMPANIES: {
                List<CompanyMasterModel> companyMasterModelList = CompanyMasterModel.getAll(data);
                if (companyMasterModelList != null && companyMasterModelList.size() > 0) {
                    setCompanySpinner(companyMasterModelList);
                }
                if (mProgressBar != null) {
                    mProgressBar.hide();
                }
            }
            break;
            case CURSOR_LOADER_LOAD_PRODUCTS: {
                mProductModelMap = ProductMasterModel.getAll(data);
            }
            break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.FAP_action_add_product:
                validateInputs();
                break;
        }
    }

    private void validateInputs() {
        if (mCompanySpinner != null && mProductsSpinner != null) {
            if (mCompanySpinner.getSelectedItemPosition() == 0) {
                showAlert("Please select the company !");
                mCompanySpinner.requestFocus();
            } else if (mProductsSpinner.getSelectedItemPosition() == 0) {
                showAlert("Please select the product");
            } else {
                if (mProgressBar != null) {
                    mProgressBar.show();
                }
                ProductMasterModel productMasterModel = (ProductMasterModel) mProductsSpinner.getSelectedItem();
                if (productMasterModel != null) {
                    DatabaseOperationService.startActionAddProductAgent(getContext(), new AgentProductModel(
                            productMasterModel.getProductId(),
                            getContext().getSharedPreferences(getContext().getPackageName(), Context.MODE_PRIVATE).getLong(KEY_USER_ID, 0),
                            2
                    ), new ResultReceiver(new Handler()) {
                        @Override
                        protected void onReceiveResult(int resultCode, Bundle resultData) {
                            if (mProgressBar != null) {
                                mProgressBar.hide();
                            }
                            if (mAddProductListener != null) {
                                mAddProductListener.onFinishListener();
                            }
                            dismiss();
                        }
                    });
                }
            }
        }
    }

    private void showAlert(String message) {
        AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(message);
        alertDialogFragment.show(getChildFragmentManager(), alertDialogFragment.getTag());
    }

    public interface AddProductListener {
        void onFinishListener();
    }
}
