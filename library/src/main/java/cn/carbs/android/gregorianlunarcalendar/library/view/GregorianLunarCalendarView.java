package cn.carbs.android.gregorianlunarcalendar.library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.opengl.Visibility;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import java.util.Calendar;

import cn.carbs.android.gregorianlunarcalendar.library.R;
import cn.carbs.android.gregorianlunarcalendar.library.data.ChineseCalendar;
import cn.carbs.android.gregorianlunarcalendar.library.util.Util;
import cn.carbswang.android.numberpickerview.library.NumberPickerView;

public class GregorianLunarCalendarView extends LinearLayout implements NumberPickerView.OnValueChangeListener{

    private static final int DEFAULT_GREGORIAN_COLOR = 0xff3388ff;
    private static final int DEFAULT_LUNAR_COLOR = 0xffee5544;
    private static final int DEFAULT_NORMAL_TEXT_COLOR = 0xFF555555;

    private static final int YEAR_START = 1901;
    private static final int YEAR_STOP = 2100;
    private static final int YEAR_SPAN = YEAR_STOP - YEAR_START + 1;

    private static final int MONTH_START = 1;
    private static final int MONTH_START_GREGORIAN = 1;
    private static final int MONTH_STOP_GREGORIAN = 12;
    private static final int MONTH_SPAN_GREGORIAN = MONTH_STOP_GREGORIAN - MONTH_START_GREGORIAN + 1;

    private static final int MONTH_START_LUNAR = 1;

    private static final int MONTH_START_LUNAR_NORMAL = 1;
    private static final int MONTH_STOP_LUNAR_NORMAL = 12;
    private static final int MONTH_SPAN_LUNAR_NORMAL = MONTH_STOP_LUNAR_NORMAL - MONTH_START_LUNAR_NORMAL + 1;

    private static final int MONTH_START_LUNAR_LEAP = 1;
    private static final int MONTH_STOP_LUNAR_LEAP = 13;
    private static final int MONTH_SPAN_LUNAR_LEAP = MONTH_STOP_LUNAR_LEAP - MONTH_START_LUNAR_LEAP + 1;

    private static final int DAY_START = 1;
    private static final int DAY_STOP = 30;

    private static final int DAY_START_GREGORIAN = 1;
    private static final int DAY_STOP_GREGORIAN = 31;
    private static final int DAY_SPAN_GREGORIAN = DAY_STOP_GREGORIAN - DAY_START_GREGORIAN + 1;

    private static final int DAY_START_LUNAR = 1;
    private static final int DAY_STOP_LUNAR = 30;
    private static final int DAY_SPAN_LUNAR = DAY_STOP_LUNAR - DAY_START_LUNAR + 1;

    private NumberPickerView mYearPickerView;
    private NumberPickerView mMonthPickerView;
    public NumberPickerView mDayPickerView;

    private int mThemeColorG = DEFAULT_GREGORIAN_COLOR;
    private int mThemeColorL = DEFAULT_LUNAR_COLOR;
    private int mNormalTextColor = DEFAULT_NORMAL_TEXT_COLOR;

    /**
     * display values
     */
    private String[] mDisplayYearsGregorian;
    private String[] mDisplayMonthsGregorian;
    private String[] mDisplayDaysGregorian;
    private String[] mDisplayYearsLunar;
    private String[] mDisplayMonthsLunar;
    private String[] mDisplayDaysLunar;

    /**
     * display values for current displayed months in lunar mode
     */
    private String[] mCurrDisplayMonthsLunar;

    private boolean mIsGregorian = true;//true is gregorian mode

    /**
     * true to use scroll anim when switch picker passively
     */
    private boolean mScrollAnim = true;

    private OnDateChangedListener mOnDateChangedListener;

    public GregorianLunarCalendarView(Context context) {
        super(context);
       initInternal(context);

    }

    public GregorianLunarCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
        initInternal(context);
    }

    public GregorianLunarCalendarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttr(context, attrs);
        initInternal(context);
    }

    public void initInternal(Context context){
        View contentView = inflate(context, R.layout.view_gregorian_lunar_calendar, this);
        mYearPickerView = (NumberPickerView) contentView.findViewById(R.id.picker_year);
        mMonthPickerView = (NumberPickerView) contentView.findViewById(R.id.picker_month);
        mDayPickerView = (NumberPickerView) contentView.findViewById(R.id.picker_day);

        mYearPickerView.setOnValueChangedListener(this);
        mMonthPickerView.setOnValueChangedListener(this);
        mDayPickerView.setOnValueChangedListener(this);
    }

    private void initAttr(Context context, AttributeSet attrs){
        if (attrs == null) {
            return;
        }
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GregorianLunarCalendarView);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if(attr == R.styleable.GregorianLunarCalendarView_glcv_ScrollAnimation){
                mScrollAnim = a.getBoolean(attr, true);
            }else if(attr == R.styleable.GregorianLunarCalendarView_glcv_GregorianThemeColor){
                mThemeColorG = a.getColor(attr, DEFAULT_GREGORIAN_COLOR);
            }if(attr == R.styleable.GregorianLunarCalendarView_glcv_LunarThemeColor){
                mThemeColorL = a.getColor(attr, DEFAULT_LUNAR_COLOR);
            }if(attr == R.styleable.GregorianLunarCalendarView_glcv_NormalTextColor){
                mNormalTextColor = a.getColor(attr, DEFAULT_NORMAL_TEXT_COLOR);
            }
        }
        a.recycle();
    }

    public void init(){
        setColor(mThemeColorG, mNormalTextColor);
        setConfigs(Calendar.getInstance(), true, false);
    }

    public void init(Calendar calendar){
        setColor(mThemeColorG, mNormalTextColor);
        setConfigs(calendar, true, false);
    }

    public void init(Calendar calendar, boolean isGregorian){
        setColor(isGregorian ? mThemeColorG : mThemeColorL, mNormalTextColor);
        setConfigs(calendar, isGregorian, false);
    }

    private void setConfigs(Calendar c, boolean isGregorian, boolean anim){
        if(c == null){
            c = Calendar.getInstance();
        }
        if(!checkCalendarAvailable(c, YEAR_START, YEAR_STOP, isGregorian)){
            c = adjustCalendarByLimit(c, YEAR_START, YEAR_STOP, isGregorian);
        }
        mIsGregorian = isGregorian;
        ChineseCalendar cc;
        if(c instanceof ChineseCalendar){
            cc = (ChineseCalendar)c;
        }else{
            cc = new ChineseCalendar(c);
        }
        setDisplayValuesForAll(cc, mIsGregorian, anim);
    }

    private Calendar adjustCalendarByLimit(Calendar c, int yearStart, int yearStop, boolean isGregorian){
        int yearGrego = c.get(Calendar.YEAR);
        if(isGregorian){
            if(yearGrego < yearStart){
                c.set(Calendar.YEAR, yearStart);
                c.set(Calendar.MONTH, MONTH_START_GREGORIAN);
                c.set(Calendar.DAY_OF_MONTH, DAY_START_GREGORIAN);
            }
            if(yearGrego > yearStop){
                c.set(Calendar.YEAR, yearStop);
                c.set(Calendar.MONTH, MONTH_STOP_GREGORIAN - 1);
                int daySway = Util.getSumOfDayInMonthForGregorianByMonth(yearStop, MONTH_STOP_GREGORIAN);
                c.set(Calendar.DAY_OF_MONTH, daySway);
            }
        }else{
            if(Math.abs(yearGrego - yearStart) < Math.abs(yearGrego - yearStop)){
                c = new ChineseCalendar(true, yearStart, MONTH_START_LUNAR, DAY_START_LUNAR);
            }else{
                int daySway = Util.getSumOfDayInMonthForLunarByMonthLunar(yearStop, MONTH_STOP_LUNAR_NORMAL);
                c = new ChineseCalendar(true, yearStop, MONTH_STOP_LUNAR_NORMAL, daySway);
            }
        }
        return c;
    }
	
    public void toGregorianMode(){
        setThemeColor(mThemeColorG);
        setGregorian(true, true);
    }

    public void toLunarMode(){
        setThemeColor(mThemeColorL);
        setGregorian(false, true);
    }

    public void setColor(int themeColor, int normalColor){
        setThemeColor(themeColor);
        setNormalColor(normalColor);
    }

    public void setThemeColor(int themeColor){
        mYearPickerView.setSelectedTextColor(themeColor);
        mYearPickerView.setHintTextColor(themeColor);
        mYearPickerView.setDividerColor(themeColor);
        mMonthPickerView.setSelectedTextColor(themeColor);
        mMonthPickerView.setHintTextColor(themeColor);
        mMonthPickerView.setDividerColor(themeColor);
        mDayPickerView.setSelectedTextColor(themeColor);
        mDayPickerView.setHintTextColor(themeColor);
        mDayPickerView.setDividerColor(themeColor);
    }

    public void setNormalColor(int normalColor){
        mYearPickerView.setNormalTextColor(normalColor);
        mMonthPickerView.setNormalTextColor(normalColor);
        mDayPickerView.setNormalTextColor(normalColor);
    }

    private void setDisplayValuesForAll(ChineseCalendar cc, boolean isGregorian, boolean anim){
        setDisplayData(isGregorian);
        initValuesForY(cc, isGregorian, anim);
        initValuesForM(cc, isGregorian, anim);
        initValuesForD(cc, isGregorian, anim);
    }
	
    /**
     *
     * @param isGregorian true is gregorian mode
     */
    private void setDisplayData(boolean isGregorian){

        if(isGregorian){
            if(mDisplayYearsGregorian == null){
                mDisplayYearsGregorian = new String[YEAR_SPAN];
                for(int i = 0; i < YEAR_SPAN; i++){
                    mDisplayYearsGregorian[i] = String.valueOf(YEAR_START + i);
                }
            }
            if(mDisplayMonthsGregorian == null){
                mDisplayMonthsGregorian = new String[MONTH_SPAN_GREGORIAN];
                for(int i = 0; i < MONTH_SPAN_GREGORIAN; i++){
                    mDisplayMonthsGregorian[i] = String.valueOf(MONTH_START_GREGORIAN + i);
                }
            }
            if(mDisplayDaysGregorian == null){
                mDisplayDaysGregorian = new String[DAY_SPAN_GREGORIAN];
                for(int i = 0; i < DAY_SPAN_GREGORIAN; i++){
                    mDisplayDaysGregorian[i] = String.valueOf(DAY_START_GREGORIAN + i);
                }
            }
        }else{
            if(mDisplayYearsLunar == null){
                mDisplayYearsLunar = new String[YEAR_SPAN];
                for(int i = 0; i < YEAR_SPAN; i++){
                    mDisplayYearsLunar[i] = Util.getLunarNameOfYear(i + YEAR_START);
                }
            }
            if(mDisplayMonthsLunar == null){
                mDisplayMonthsLunar = new String[MONTH_SPAN_GREGORIAN];
                for(int i = 0; i < MONTH_SPAN_GREGORIAN; i++){
                    mDisplayMonthsLunar[i] = Util.getLunarNameOfMonth(i + 1);
                }
            }
            if(mDisplayDaysLunar == null){
                mDisplayDaysLunar = new String[DAY_SPAN_LUNAR];
                for(int i = 0; i < DAY_SPAN_LUNAR; i++){
                    mDisplayDaysLunar[i] = Util.getLunarNameOfDay(i + 1);
                }
            }
        }
    }

    //without scroll animation when init
    private void initValuesForY(ChineseCalendar cc, boolean isGregorian, boolean anim){
        if(isGregorian){
            int yearSway = cc.get(Calendar.YEAR);
            setValuesForPickerView(mYearPickerView, yearSway, YEAR_START, YEAR_STOP, mDisplayYearsGregorian, false, anim);
        }else{
            int yearSway = cc.get(ChineseCalendar.CHINESE_YEAR);
            setValuesForPickerView(mYearPickerView, yearSway, YEAR_START, YEAR_STOP, mDisplayYearsLunar, false, anim);
        }
    }
    
    private void initValuesForM(ChineseCalendar cc, boolean isGregorian, boolean anim){
        int monthStart;
        int monthStop;
        int monthSway;
        String[] newDisplayedVales = null;
        if(isGregorian){
            monthStart = MONTH_START_GREGORIAN;
            monthStop = MONTH_STOP_GREGORIAN;
            monthSway = cc.get(Calendar.MONTH) + 1;
            newDisplayedVales = mDisplayMonthsGregorian;
        }else{
            int monthLeap = Util.getMonthLeapByYear(cc.get(ChineseCalendar.CHINESE_YEAR));
            if(monthLeap == 0){
                monthStart = MONTH_START_LUNAR_NORMAL;
                monthStop = MONTH_STOP_LUNAR_NORMAL;
                monthSway = cc.get(ChineseCalendar.CHINESE_MONTH);
                newDisplayedVales = mDisplayMonthsLunar;
            }else{
                monthStart = MONTH_START_LUNAR_LEAP;
                monthStop = MONTH_STOP_LUNAR_LEAP;
                monthSway = Util.convertMonthLunarToMonthSway(cc.get(ChineseCalendar.CHINESE_MONTH), monthLeap);
                newDisplayedVales = Util.getLunarMonthsNamesWithLeap(monthLeap);
            }
        }
        setValuesForPickerView(mMonthPickerView, monthSway, monthStart, monthStop, newDisplayedVales, false, anim);
    }

    private void initValuesForD(ChineseCalendar cc, boolean isGregorian, boolean anim){
        if(isGregorian){
            int dayStart = DAY_START_GREGORIAN;
            int dayStop = Util.getSumOfDayInMonthForGregorianByMonth(cc.get(Calendar.YEAR), cc.get(Calendar.MONTH) + 1);
            int daySway = cc.get(Calendar.DAY_OF_MONTH);
            mDayPickerView.setHintText(getContext().getResources().getString(R.string.day));
            setValuesForPickerView(mDayPickerView, daySway, dayStart, dayStop, mDisplayDaysGregorian, false, anim);
        }else{
            int dayStart = DAY_START_LUNAR;
            int dayStop = Util.getSumOfDayInMonthForLunarByMonthLunar(cc.get(ChineseCalendar.CHINESE_YEAR), cc.get(ChineseCalendar.CHINESE_MONTH));
            int daySway = cc.get(ChineseCalendar.CHINESE_DATE);
            mDayPickerView.setHintText("");
            setValuesForPickerView(mDayPickerView, daySway, dayStart, dayStop, mDisplayDaysLunar, false, anim);
        }
    }
    
    private void setValuesForPickerView(NumberPickerView pickerView, int newSway, int newStart, int newStop,
                                        String[] newDisplayedVales, boolean needRespond, boolean anim){

        if(newDisplayedVales == null){
            throw new IllegalArgumentException("newDisplayedVales should not be null.");
        }else if(newDisplayedVales.length == 0){
            throw new IllegalArgumentException("newDisplayedVales's length should not be 0.");
        }
        int newSpan = newStop - newStart + 1;
        if(newDisplayedVales.length < newSpan){
            throw new IllegalArgumentException("newDisplayedVales's length should not be less than newSpan.");
        }

        int oldStart = pickerView.getMinValue();
        int oldStop = pickerView.getMaxValue();
        int oldSpan = oldStop - oldStart + 1;
        int fromValue = pickerView.getValue();
        pickerView.setMinValue(newStart);
        if(newSpan > oldSpan){
            pickerView.setDisplayedValues(newDisplayedVales);
            pickerView.setMaxValue(newStop);
        }else{
            pickerView.setMaxValue(newStop);
            pickerView.setDisplayedValues(newDisplayedVales);
        }
        if(mScrollAnim && anim){
            int toValue = newSway;
            if(fromValue < newStart){
                fromValue = newStart;
            }
            pickerView.smoothScrollToValue(fromValue, toValue, needRespond);
        }else{
            pickerView.setValue(newSway);
        }
    }

    @Override
    public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
        if(picker == null) return;
        if(picker == mYearPickerView){
            passiveUpdateMonthAndDay(oldVal, newVal, mIsGregorian);
        }else if(picker == mMonthPickerView){
            int fixYear = mYearPickerView.getValue();
            passiveUpdateDay(fixYear, fixYear, oldVal, newVal, mIsGregorian);
        }else if(picker == mDayPickerView){
            if(mOnDateChangedListener != null){
                mOnDateChangedListener.onDateChanged(getCalendarData());
            }
        }
    }

    private void passiveUpdateMonthAndDay(int oldYearFix, int newYearFix, boolean isGregorian){
        int oldMonthSway = mMonthPickerView.getValue();
        int oldDaySway = mDayPickerView.getValue();

        if(isGregorian){
            int newMonthSway = oldMonthSway;
            int oldDayStop = Util.getSumOfDayInMonth(oldYearFix, oldMonthSway, true);
            int newDayStop = Util.getSumOfDayInMonth(newYearFix, newMonthSway, true);

            if(oldDayStop == newDayStop){
                if(mOnDateChangedListener != null){
                    mOnDateChangedListener.onDateChanged(getCalendarData(newYearFix, newMonthSway, oldDaySway, isGregorian));
                }
                return;
            }
            int newDaySway = (oldDaySway <= newDayStop) ? oldDaySway : newDayStop;
            setValuesForPickerView(mDayPickerView, newDaySway, DAY_START, newDayStop, mDisplayDaysGregorian, true, true);
            if(mOnDateChangedListener != null){
                mOnDateChangedListener.onDateChanged(getCalendarData(newYearFix, newMonthSway, newDaySway, isGregorian));
            }
            return;
        }else{
            int newMonthSway = 0;

            int newYearFixMonthLeap = Util.getMonthLeapByYear(newYearFix);//1.????????????year???????????????
            int oldYearFixMonthLeap = Util.getMonthLeapByYear(oldYearFix);//2.????????????year???????????????

            if(newYearFixMonthLeap == oldYearFixMonthLeap){
                //only update day picker
                newMonthSway = oldMonthSway;

                int oldMonthLunar = Util.convertMonthSwayToMonthLunar(oldMonthSway, oldYearFixMonthLeap);
                int newMonthLunar = Util.convertMonthSwayToMonthLunar(newMonthSway, newYearFixMonthLeap);
                int oldDayStop = Util.getSumOfDayInMonthForLunarByMonthLunar(oldYearFix, oldMonthLunar);
                int newDayStop = Util.getSumOfDayInMonthForLunarByMonthLunar(newYearFix, newMonthLunar);

                if(oldDayStop == newDayStop){
                    if(mOnDateChangedListener != null){
                        mOnDateChangedListener.onDateChanged(getCalendarData(newYearFix, newMonthSway, oldDaySway, isGregorian));
                    }
                    return;
                }else{
                    int newDaySway = (oldDaySway <= newDayStop) ? oldDaySway : newDayStop;
                    setValuesForPickerView(mDayPickerView, newDaySway, DAY_START, newDayStop, mDisplayDaysLunar, true, true);
                    if(mOnDateChangedListener != null){
                        mOnDateChangedListener.onDateChanged(getCalendarData(newYearFix, newMonthSway, newDaySway, isGregorian));
                    }
                    return;
                }
            }else{
                //?????????????????????????????????????????????????????????????????????????????????newMonthSway???????????????????????????????????????????????????????????????newMonthSway?????????????????????
                //?????????????????????
                mCurrDisplayMonthsLunar = Util.getLunarMonthsNamesWithLeap(newYearFixMonthLeap);

                //????????????
                int oldMonthLunar = Util.convertMonthSwayToMonthLunar(oldMonthSway, oldYearFixMonthLeap);
                int oldMonthLunarAbs = Math.abs(oldMonthLunar);
                newMonthSway = Util.convertMonthLunarToMonthSway(oldMonthLunarAbs, newYearFixMonthLeap);
                setValuesForPickerView(mMonthPickerView, newMonthSway, MONTH_START_LUNAR,
                        newYearFixMonthLeap == 0 ? MONTH_STOP_LUNAR_NORMAL : MONTH_STOP_LUNAR_LEAP, mCurrDisplayMonthsLunar, false, true);

                //?????????????????????
                int oldDayStop = Util.getSumOfDayInMonth(oldYearFix, oldMonthSway, false);
                int newDayStop = Util.getSumOfDayInMonth(newYearFix, newMonthSway, false);
                if(oldDayStop == newDayStop){
                    if(mOnDateChangedListener != null){
                        mOnDateChangedListener.onDateChanged(getCalendarData(newYearFix, newMonthSway, oldDaySway, isGregorian));
                    }
                    return;//???????????????
                }else{
                    int newDaySway = (oldDaySway <= newDayStop) ? oldDaySway : newDayStop;
                    setValuesForPickerView(mDayPickerView, newDaySway, DAY_START, newDayStop, mDisplayDaysLunar, true, true);
                    if(mOnDateChangedListener != null){
                        mOnDateChangedListener.onDateChanged(getCalendarData(newYearFix, newMonthSway, newDaySway, isGregorian));
                    }
                    return;
                }
            }
        }
    }
	
    private void passiveUpdateDay(int oldYear, int newYear, int oldMonth, int newMonth, boolean isGregorian){
        int oldDaySway = mDayPickerView.getValue();

        int oldDayStop = Util.getSumOfDayInMonth(oldYear, oldMonth, isGregorian);
        int newDayStop = Util.getSumOfDayInMonth(newYear, newMonth, isGregorian);

        if(oldDayStop == newDayStop){
            if(mOnDateChangedListener != null){
                mOnDateChangedListener.onDateChanged(getCalendarData(newYear, newMonth, oldDaySway, isGregorian));
            }
            return;//???????????????
        }else{
            int newDaySway = oldDaySway <= newDayStop ? oldDaySway : newDayStop;
            setValuesForPickerView(mDayPickerView, newDaySway, DAY_START, newDayStop, isGregorian ? mDisplayDaysGregorian : mDisplayDaysLunar, true, true);
            if(mOnDateChangedListener != null){
                mOnDateChangedListener.onDateChanged(getCalendarData(newYear, newMonth, newDaySway, isGregorian));
            }
            return;
        }
    }
	
    public void setGregorian(boolean isGregorian, boolean anim){
        if(mIsGregorian == isGregorian){
            return;
        }

        ChineseCalendar cc = (ChineseCalendar)getCalendarData().getCalendar();//??????mIsGregorian????????????
        if(!checkCalendarAvailable(cc, YEAR_START, YEAR_STOP, isGregorian)){
            cc = (ChineseCalendar)adjustCalendarByLimit(cc, YEAR_START, YEAR_STOP, isGregorian);//?????????????????????????????????
        }
        mIsGregorian = isGregorian;//??????mIsGregorian?????????
        setConfigs(cc, isGregorian, anim);//????????????????????????
    }

    private boolean checkCalendarAvailable(Calendar cc, int yearStart, int yearStop, boolean isGregorian){
        int year = isGregorian ? cc.get(Calendar.YEAR) : ((ChineseCalendar)cc).get(ChineseCalendar.CHINESE_YEAR);
        return (yearStart <= year) && (year <= yearStop);
    }

    public View getNumberPickerYear(){
        return mYearPickerView;
    }

    public View getNumberPickerMonth(){
        return mMonthPickerView;
    }

    public View getNumberPickerDay(){
        return mDayPickerView;
    }

    public void setNumberPickerYearVisibility(int visibility){
        setNumberPickerVisibility(mYearPickerView, visibility);
    }

    public void setNumberPickerMonthVisibility(int visibility){
        setNumberPickerVisibility(mMonthPickerView, visibility);
    }

    public void setNumberPickerDayVisibility(int visibility){
        setNumberPickerVisibility(mDayPickerView, visibility);
    }

    public void setNumberPickerVisibility(NumberPickerView view, int visibility){
        if(view.getVisibility() == visibility){
            return;
        }else if(visibility == View.GONE || visibility == View.VISIBLE || visibility == View.INVISIBLE){
            view.setVisibility(visibility);
        }
    }

    public boolean getIsGregorian(){
        return mIsGregorian;
    }

    private CalendarData getCalendarData(int pickedYear, int pickedMonthSway, int pickedDay, boolean mIsGregorian){
        return new CalendarData(pickedYear, pickedMonthSway, pickedDay, mIsGregorian);
    }

    public CalendarData getCalendarData(){
        int pickedYear = mYearPickerView.getValue();
        int pickedMonthSway = mMonthPickerView.getValue();
        int pickedDay = mDayPickerView.getValue();
        return new CalendarData(pickedYear, pickedMonthSway, pickedDay, mIsGregorian);
    }

    public static class CalendarData{
        public boolean isGregorian = false;
        public int pickedYear;
        public int pickedMonthSway;
        public int pickedDay;

        /**
         * ??????????????????????????????
         * Gregorian : //??????
         *      chineseCalendar.get(Calendar.YEAR)              //???????????????????????????[1900 ~ 2100]
         *      chineseCalendar.get(Calendar.MONTH) + 1         //???????????????????????????[1 ~ 12]
         *      chineseCalendar.get(Calendar.DAY_OF_MONTH)      //????????????????????????[1 ~ 30]
         *
         * Lunar
         *      chineseCalendar.get(ChineseCalendar.CHINESE_YEAR)   //???????????????????????????[1900 ~ 2100]
         *      chineseCalendar.get(ChineseCalendar.CHINESE_MONTH)) //???????????????????????????[(-12) ~ (-1)] || [1 ~ 12]
         *                                                          //?????????????????????????????????????????????
         *                                                          //????????????????????????????????????????????????
         *      calendar.get(ChineseCalendar.CHINESE_DATE)         //????????????????????????[1 ~ 30]
         */
        public ChineseCalendar chineseCalendar;

        /**
         * model??????????????????
         * @param pickedYear
         * 			???
         * @param pickedMonthSway
         * 			????????????????????????1????????????????????????????????????????????????????????????
         * @param pickedDay
         * 			?????????1??????????????????????????????????????????
         * @param isGregorian
         * 			???????????????
         */
        public CalendarData(int pickedYear, int pickedMonthSway, int pickedDay, boolean isGregorian) {
            this.pickedYear = pickedYear;
            this.pickedMonthSway = pickedMonthSway;
            this.pickedDay = pickedDay;
            this.isGregorian = isGregorian;
            initChineseCalendar();
        }

        /**
         * ?????????????????????chineseCalendar?????????????????????????????????????????????????????????????????????????????????????????????
         */
        private void initChineseCalendar(){
            if(isGregorian){
                chineseCalendar = new ChineseCalendar(pickedYear, pickedMonthSway - 1, pickedDay);//????????????????????????
            }else{
                int y = pickedYear;
                int m = Util.convertMonthSwayToMonthLunarByYear(pickedMonthSway, pickedYear);
                int d = pickedDay;

                chineseCalendar = new ChineseCalendar(true, y, m, d);
            }
        }

        public Calendar getCalendar(){
            return chineseCalendar;
        }
    }

    public interface OnDateChangedListener{
        void onDateChanged(CalendarData calendarData);
    }

    public void setOnDateChangedListener(OnDateChangedListener listener){
        mOnDateChangedListener = listener;
    }

}