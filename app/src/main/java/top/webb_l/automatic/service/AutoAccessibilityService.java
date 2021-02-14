package top.webb_l.automatic.service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import top.webb_l.automatic.acitivity.UseScriptActivity;
import top.webb_l.automatic.model.Scripts;
import top.webb_l.automatic.model.Steps;

public class AutoAccessibilityService extends AccessibilityService {
    public static boolean serviceStatus = false;
    private final String TAG = getClass().getName();
    private HashSet<String> controlCheckList;
    private HashSet<AccessibilityNodeInfo> nodeInfoHashSet = new HashSet<>();
    private HashSet<AccessibilityNodeInfo> nodeHashSet = new HashSet<>();
    private ArrayList<Steps> findText = new ArrayList<>();
    private ArrayList<Steps> findId = new ArrayList<>();
    private boolean status = true;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Scripts script = UseScriptActivity.script;
        if (TextUtils.isEmpty(event.getPackageName()) || !event.getPackageName().toString().contains(script.getPackageName())) {
            return;
        }
        if (!event.getClassName().toString().contains(script.getActivity())) {
            return;
        }
        // 获取页面全部节点
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (!status) {
            return;
        }
        status = false;
        UseScriptActivity.scriptLog.setText(outLogMessage(UseScriptActivity.scriptLog.getText(), "成功", "开始遍历页面中的全部控件！"));
        getAllNodeInfo(root);
        UseScriptActivity.scriptLog.setText(outLogMessage(UseScriptActivity.scriptLog.getText(), "成功", "结束遍历！"));
        UseScriptActivity.scriptLog.setText(outLogMessage(UseScriptActivity.scriptLog.getText(), "成功", "根据步骤分类来进行查找控件！"));
        findType(nodeInfoHashSet);
        UseScriptActivity.scriptLog.setText(outLogMessage(UseScriptActivity.scriptLog.getText(), "成功", "结束查找！"));
        UseScriptActivity.scriptLog.setText(outLogMessage(UseScriptActivity.scriptLog.getText(), "成功", "根据查找到控件执行相应的事件！"));
        eventNode(nodeHashSet);
        UseScriptActivity.scriptLog.setText(outLogMessage(UseScriptActivity.scriptLog.getText(), "成功", "结束执行事件！"));
    }

    private void eventNode(HashSet<AccessibilityNodeInfo> nodeHashSet) {
        for (AccessibilityNodeInfo nodeInfo : nodeHashSet) {
            for (Steps step : findText) {
                if (step.getSearchContent().contains(nodeInfo.getText())) {
                    switch (step.getEvent()) {
                        case 1:
                            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            UseScriptActivity.scriptLog.setText(outLogMessage(UseScriptActivity.scriptLog.getText(), "成功", "执行" + nodeInfo.getClassName().toString() + "点击事件！"));
                            break;
                        case 2:
                            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);
                            UseScriptActivity.scriptLog.setText(outLogMessage(UseScriptActivity.scriptLog.getText(), "成功", "执行" + nodeInfo.getClassName().toString() + "长按事件！"));
                            break;
                        case 3:
                            UseScriptActivity.scriptLog.setText(outLogMessage(UseScriptActivity.scriptLog.getText(), "成功", "执行" + nodeInfo.getClassName().toString() + "复制事件！"));
                            UseScriptActivity.scriptLog.setText(UseScriptActivity.scriptLog.getText() + "\n" + nodeInfo.getText());
                            break;
                        case 4:
                            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                            Bundle bundle = new Bundle();
                            bundle.putString(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, step.getPasteContent());
                            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, bundle);
                            UseScriptActivity.scriptLog.setText(outLogMessage(UseScriptActivity.scriptLog.getText(), "成功", "执行" + nodeInfo.getClassName().toString() + "粘贴事件！"));
                            break;
                        default:
                            UseScriptActivity.scriptLog.setText(outLogMessage(UseScriptActivity.scriptLog.getText(), "错误", nodeInfo.getClassName().toString() + "找不到能执行的事件！"));
                            break;
                    }
                }
            }
        }
    }

    /**
     * 根据搜索类型来查找
     *
     * @param nodeInfoHashSet
     */
    private void findType(HashSet<AccessibilityNodeInfo> nodeInfoHashSet) {
        for (AccessibilityNodeInfo node : nodeInfoHashSet) {
            if (!TextUtils.isEmpty(node.getText())) {
                for (Steps step : findText) {
                    if (step.getSearchContent().contains(node.getText())) {
                        nodeHashSet.add(node);
                    }
                }
            } else {
                for (Steps step : findId) {
                    Log.d(TAG, "findType: " + node);
                    nodeHashSet.add(node);
                }
            }
        }
    }

    /**
     * 根据成员属性nodeInfoHashSet来获取所有节点
     *
     * @param node
     */
    private void getAllNodeInfo(AccessibilityNodeInfo node) {
        if (node == null || node.getChildCount() <= 0) {
            return;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            for (String control : controlCheckList) {
                if (child != null && child.getClassName() != null && child.getClassName().toString().contains(control)) {
                    UseScriptActivity.scriptLog.setText(outLogMessage(UseScriptActivity.scriptLog.getText(), "成功", "查找到" + child.getClassName().toString()));
                    nodeInfoHashSet.add(child);
                }
            }
            getAllNodeInfo(child);
        }
    }


    /**
     * 回调以中断可访问性反馈。
     */
    @Override
    public void onInterrupt() {
        Toast.makeText(this, "onInterrupt", Toast.LENGTH_SHORT).show();
    }

    /**
     * 开启服务触发的方法。
     */
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        serviceStatus = true;
        UseScriptActivity.scriptLog.setText(outLogMessage(UseScriptActivity.scriptLog.getText(), "成功", "开始执行！"));
        controlCheckList = new HashSet<>();
        for (Steps step : UseScriptActivity.steps) {
            switch (step.getControl()) {
                case 1:
                    controlCheckList.add("android.widget.Button");
                    break;
                case 2:
                    controlCheckList.add("android.widget.ImageView");
                    break;
                case 3:
                    controlCheckList.add("android.widget.TextView");
                    break;
                case 4:
                    controlCheckList.add("android.widget.RadioButton");
                    break;
                case 5:
                    controlCheckList.add("android.widget.CheckBox");
                    break;
                case 6:
                    controlCheckList.add("android.widget.EditText");
                    break;
                default:
                    break;
            }
            switch (step.getSearchType()) {
                case 1:
                    findText.add(step);
                    break;
                case 2:
                    findId.add(step);
                    break;
                default:
                    break;
            }
        }
        performGlobalAction(GLOBAL_ACTION_BACK);
    }


    /**
     * 服务关闭触发方法。
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        UseScriptActivity.scriptLog.setText(outLogMessage(UseScriptActivity.scriptLog.getText(), "成功", "执行结束！"));
        serviceStatus = false;
    }

    public String outLogMessage(CharSequence oldMessage, String status, String message) {
        @SuppressLint("SimpleDateFormat")
        String nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        return oldMessage + "\n时间：" + nowDate + " 状态：" + status + " 信息：" + message;
    }
}