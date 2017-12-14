package com.omni.omniguidersdkdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.omni.omninavi.omninavi.model.OGFloor;
import com.omni.omninavi.omninavi.model.OGFloors;
import com.omni.omninavi.omninavi.model.OGPOI;

/**
 * Created by wiliiamwang on 06/12/2017.
 */

public class OptionsActivity extends Activity {

    private static boolean IS_TEST_VERSION = false;

    public static boolean isTestVersion() {
        return IS_TEST_VERSION;
    }

    private TextInputLayout poiIdTIL;
    private TextInputEditText poiIdTIET;
    private TextInputEditText poiNameTIET;
    private AppCompatButton startNaviBtn;
    private AppCompatButton openMapBtn;
    private Spinner floorSpinner;
    private Spinner poiSpinner;

    private OGFloor mGoToFloor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_options);

        poiIdTIL = (TextInputLayout) findViewById(R.id.activity_options_til_poi_id);
        poiIdTIET = (TextInputEditText) findViewById(R.id.activity_options_tiet_poi_id);
        poiNameTIET = (TextInputEditText) findViewById(R.id.activity_options_tiet_poi_name);

        startNaviBtn = (AppCompatButton) findViewById(R.id.activity_options_start_navi);
        startNaviBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String poiId = poiIdTIET.getText().toString().trim();
                String poiName = poiNameTIET.getText().toString().trim();
                String gotoFloor = mGoToFloor.getNumber();
                String gotoFloorPlanId = mGoToFloor.getFloorPlanId();

                if (TextUtils.isEmpty(poiId)) {
                    poiIdTIL.setError("POI id is required.");
                    return;
                } else {
                    poiIdTIL.setError("");
                }

                OGMapsActivity.navigationTo(OptionsActivity.this,
                        poiId,
                        poiName,
                        "1",
                        gotoFloor,
                        gotoFloorPlanId);
            }
        });

        openMapBtn = (AppCompatButton) findViewById(R.id.activity_options_open_map);
        openMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OptionsActivity.this, OGMapsActivity.class));
            }
        });

        floorSpinner = (Spinner) findViewById(R.id.activity_options_spinner_floors);
        poiSpinner = (Spinner) findViewById(R.id.activity_options_spinner_pois);

        DataCacheManager.getInstance().getBuildingFloors(this,
                new DataCacheManager.GetBuildingFloorsListener() {
                    @Override
                    public void onFinished(final OGFloors floors) {
                        if (floors != null) {
                            floorSpinner.setAdapter(new OptionsSpinnerAdapter(OptionsActivity.this, floors.getData()));
                            floorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    mGoToFloor = floors.getData()[position];
                                    onFloorSelected(floors.getData()[position]);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }
                    }
                });

        final Switch s = (Switch) findViewById(R.id.activity_options_switch);
        s.setVisibility(View.GONE);
        s.setChecked(IS_TEST_VERSION);
        s.setText(IS_TEST_VERSION ? "測試版" : "正式版");
        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                IS_TEST_VERSION = !IS_TEST_VERSION;
                s.setText(IS_TEST_VERSION ? "測試版" : "正式版");
//                if (!IS_TEST_VERSION) {
//                    DataCacheManager.getInstance().setUserCurrentFloorLevel("");
//                    DataCacheManager.getInstance().setUserCurrentFloorPlanId("");
//                }
            }
        });
    }

    private void onFloorSelected(final OGFloor floor) {
        if (floor != null && floor.getPois() != null) {
            poiSpinner.setAdapter(new OptionsSpinnerAdapter(OptionsActivity.this, floor.getPois()));
            poiSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    OGPOI poi = floor.getPois()[position];
                    poiIdTIET.setText(poi.getId());
                    poiNameTIET.setText(poi.getName());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    class OptionsSpinnerAdapter<T> extends BaseAdapter {

        private T[] mList;
        private Context mContext;

        public OptionsSpinnerAdapter(Context context, T[] dataList) {
            mList = dataList;
            mContext = context;
        }

        public void updateData(T[] dataList) {
            mList = dataList;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mList.length;
        }

        @Override
        public Object getItem(int position) {
            return mList[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.options_spinner_item_view, null, false);
                viewHolder = new ViewHolder();
                viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.options_spinner_item_view_tv_title);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Object object = getItem(position);

            if (object instanceof OGFloor) {
                OGFloor floor = (OGFloor) object;
                viewHolder.titleTextView.setText(floor.getDesc());
            } else if (object instanceof OGPOI) {
                OGPOI poi = (OGPOI) object;
                viewHolder.titleTextView.setText(poi.getName() + ", " + poi.getId());
            }

            return convertView;
        }

        class ViewHolder {
            TextView titleTextView;
        }
    }
}
