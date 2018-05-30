package com.chen.answerer.util;

import com.chen.answerer.window.MainWindow;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.LowLevelKeyboardProc;

public class KeyListener {
    private final LowLevelKeyboardProc keyboardHook;
    private static WinUser.HHOOK hhk;
    private final User32 lib = User32.INSTANCE;
    private final WinDef.HMODULE hMod;

    private MainWindow window;
    public KeyListener(MainWindow window) {
        this.window = window;
        hMod = Kernel32.INSTANCE.GetModuleHandle(null);
        keyboardHook = (nCode, wParam, info) -> {
            if (nCode >= 0) {
                switch(wParam.intValue()) {
                    case WinUser.WM_KEYUP:
                        break;
                    case WinUser.WM_KEYDOWN:
                        if (info.vkCode == 192) {
                            try {
                                window.getQuestion();
                            } catch (Exception e) {
                                System.out.println("异常");
                                e.printStackTrace();
                            }
                        }
                        break;
                    case WinUser.WM_SYSKEYUP:
                        break;
                    case WinUser.WM_SYSKEYDOWN:
                        break;
                }
            }

            Pointer ptr = info.getPointer();
            long peer = Pointer.nativeValue(ptr);
            return lib.CallNextHookEx(hhk, nCode, wParam, new WinDef.LPARAM(peer));
        };
        hhk = lib.SetWindowsHookEx(WinUser.WH_KEYBOARD_LL, keyboardHook, hMod, 0);
//         This bit never returns from GetMessage
        int result;
        WinUser.MSG msg = new WinUser.MSG();
        while ((result = lib.GetMessage(msg, null, 0, 0)) != 0) {
            if (result == -1) {
                  System.err.println("error in get message");
                break;
            }
            else {
                  System.err.println("got message");
                lib.TranslateMessage(msg);
                lib.DispatchMessage(msg);
            }
        }
        lib.UnhookWindowsHookEx(hhk);
    }


}
