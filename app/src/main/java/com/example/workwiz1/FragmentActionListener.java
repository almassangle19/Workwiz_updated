package com.example.workwiz1;

import android.os.Bundle;

interface FragmentActionListener {
    String ACTION_KEY = "Key";
    int ACTION_VALUE_BACK_TO_HOME = 11;

    Void actionPerformed(Bundle bundle);
}
