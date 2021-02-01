package top.webb_l.automatic.service;

import android.accessibilityservice.AccessibilityService;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.HashSet;

public class AutoAccessibilityService extends AccessibilityService {
    private final String TAG = getClass().getName();
    public static AutoAccessibilityService mService;
    public static String[] findText = null;
    private boolean isNodeList = false;
    private final HashSet<AccessibilityNodeInfo> nodeList = new HashSet<>();

    public AutoAccessibilityService() {
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null || !event.getPackageName().toString().contains("com.wisedu.cpdaily")) {
            Log.d(TAG, "onAccessibilityEvent: 打开APP错误");
            return;
        }

        if (!isNodeList) {
            isNodeList = true;
            AccessibilityNodeInfo rootNode = getRootInActiveWindow();
            if (rootNode == null) {
                return;
            }
            getPackageNameNode(rootNode, "android.widget.CheckBox");
            findTextNode(nodeList, findText);
        }
    }

    /**
     * 根据文本来查找节点
     *
     * @param nodeList 节点列表 HashSet<AccessibilityNodeInfo>
     * @param findText 要查找的文本 String[]
     */
    private void findTextNode(HashSet<AccessibilityNodeInfo> nodeList, String[] findText) {
        if (nodeList.size() <= 0 || findText.length <= 0) {
            return;
        }
        for (AccessibilityNodeInfo node : nodeList) {
            if (!node.isChecked()) {
                for (String text : findText) {
                    if (String.valueOf(node.getText()).trim().contains(text.trim())) {
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mService.disableSelf();
        }
        mService.onDestroy();
        nodeList.clear();
    }

    /**
     * 根据包名来获取节点
     *
     * @param rootNode    根节点 AccessibilityNodeInfo
     * @param packageName 控件包名 packageName
     */
    private void getPackageNameNode(AccessibilityNodeInfo rootNode, String packageName) {
        for (int i = 0; i < rootNode.getChildCount(); i++) {
            AccessibilityNodeInfo node = rootNode.getChild(i);
            if (node == null) {
                break;
            }
            if (node.getChildCount() > 0) {
                getPackageNameNode(node, packageName);
            }
            if (node.isEnabled() && node.isClickable() && (node.getClassName().equals(packageName))) {
                nodeList.add(node);
            }
        }
    }

    @Override
    public void onInterrupt() {
        mService = null;
    }

    /**
     * 服务在设置中启用
     */
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        mService = this;
    }


    /**
     * 判断服务是否开启
     *
     * @return 如果开启就返回true，关闭则返回false
     */
    public static boolean isStatus() {
        return mService != null;
    }

}