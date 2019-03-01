
package net.iaround.tools;


import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;


public class EditTextUtil {

    /**
     * 限制EditText的输入字数
     *
     * @param editText
     * @param limitLength
     */
    static public void autoLimitLength(EditText editText, long limitLength) {
        editText.addTextChangedListener(new AutoLimitLengthTextWatcher(editText,
                limitLength));
    }

    /**
     * 限制EditText的输入字数，并每次输入都产生回调
     *
     * @param editText
     * @param limitLength
     * @param listener
     */
    static public void autoLimitLength(EditText editText, long limitLength,
                                       OnLimitLengthListener listener) {
        editText.addTextChangedListener(new AutoLimitLengthTextWatcher(editText,
                limitLength, listener));
    }

    static public class AutoLimitLengthTextWatcher implements TextWatcher {
        private EditText mEditText;
        // private int mEditStart;
        // private int mEditEnd;
        private String strContent = "";
        private long mLimitLength;
        private OnLimitLengthListener mOnLimitLengthListener;

        public AutoLimitLengthTextWatcher(EditText editText, long limitLength) {
            this(editText, limitLength, null);
        }

        public AutoLimitLengthTextWatcher(EditText editText, long limitLength,
                                          OnLimitLengthListener listener) {
            if (editText == null) {
                throw new IllegalArgumentException("EditText is null!");
            }

            if (limitLength < 1) {
                throw new IllegalArgumentException("limitLength must be >= 1 !");
            }

            mEditText = editText;
            mLimitLength = limitLength;
            mOnLimitLengthListener = listener;
        }

        @Override
        public void afterTextChanged(Editable s) {

            if (mOnLimitLengthListener != null) {
                mOnLimitLengthListener.limit(mLimitLength, getInputCount());
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mEditText.removeTextChangedListener(this);
            boolean ischange = false;
            int len = StringUtil.getLengthCN1(s.toString());
            int rrLen = s.length();

            if (len > mLimitLength || rrLen > mLimitLength) {
                strContent = mEditText.getText().toString();

                int lenLimit = (int) (rrLen - mLimitLength);

                int realLen = rrLen - lenLimit;
                if (len > rrLen) {
                    lenLimit = (int) (len - mLimitLength) / 2;
                    realLen = rrLen - lenLimit;
                }

                strContent = strContent.substring(0, realLen);
                mEditText.setText(strContent);
//				KeyEvent keyEventDown = new KeyEvent( KeyEvent.ACTION_DOWN ,
//						KeyEvent.KEYCODE_DEL );
//				mEditText.onKeyDown( KeyEvent.KEYCODE_DEL , keyEventDown );
                strContent = mEditText.getText().toString();

                mEditText.setSelection(strContent.length());

                ischange = true;
            }
//			while ( calculateLength( s.toString() ) > mLimitLength )
//			{ // 当输入字符个数超过限制的大小时，进行截断操作
//
//				KeyEvent keyEventDown = new KeyEvent( KeyEvent.ACTION_DOWN ,
//						KeyEvent.KEYCODE_DEL );
//				mEditText.onKeyDown( KeyEvent.KEYCODE_DEL , keyEventDown );
//
//			}
            if (!strContent.equals(mEditText.getText().toString()) || ischange) {
                int sel = mEditText.getSelectionEnd();
                strContent = mEditText.getText().toString();
                if (ischange) {
                    FaceManager.getInstance(mEditText.getContext()).parseIconForEditText(mEditText.getContext(), mEditText);
                } else {
                    FaceManager.getInstance(mEditText.getContext()).parseIconForEditText(mEditText.getContext(), mEditText, start, before, count);
                }
                int last = mEditText.getText().toString().length();
                sel = last >= sel ? sel : last;
                mEditText.setSelection(sel);
            }


            // 恢复监听器
            mEditText.addTextChangedListener(this);
        }


        /**
         * 获取用户输入的分享内容字数
         */
        private int getInputCount() {
            return FaceManager.calculateLength(mEditText.getText().toString());
        }
    }

    public interface OnLimitLengthListener {

        /**
         * 输入限制回调
         *
         * @param limitCount 限制字数
         * @param overCount  剩余字数
         */
        void limit(long limitCount, long overCount);
    }
}
