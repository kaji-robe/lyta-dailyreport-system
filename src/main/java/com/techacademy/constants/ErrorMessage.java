package com.techacademy.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// エラーメッセージ用クラス
public class ErrorMessage {

    // エラーメッセージ情報マップ
    private final static Map<ErrorKinds, List<String>> errorMessageMap = new HashMap<ErrorKinds, List<String>>() {
        private static final long serialVersionUID = 1L;

        {
            // パスワード空白チェック用エラーメッセージ
            put(ErrorKinds.BLANK_ERROR, new ArrayList<String>(Arrays.asList("passwordError", "値を入力してください")));
            // パスワードの半角英数字チェック用エラーメッセージ
            put(ErrorKinds.HALFSIZE_ERROR,
                    new ArrayList<String>(Arrays.asList("passwordError", "パスワードは半角英数字のみで入力してください")));
            // パスワードの8文字～16文字チェック用エラーメッセージ
            put(ErrorKinds.RANGECHECK_ERROR, new ArrayList<String>(Arrays.asList("passwordError", "8文字以上16文字以下で入力してください")));
            // 従業員番号重複チェック用エラーメッセージ
            put(ErrorKinds.DUPLICATE_EXCEPTION_ERROR, new ArrayList<String>(Arrays.asList("codeError", "既に登録されている社員番号です")));
            // 従業員番号重複チェック(例外)用エラーメッセージ
            put(ErrorKinds.DUPLICATE_ERROR, new ArrayList<String>(Arrays.asList("codeError", "既に登録されている社員番号です")));
            // ログイン中の従業員削除チェック用エラーメッセージ
            put(ErrorKinds.LOGINCHECK_ERROR, new ArrayList<String>(Arrays.asList("deleteError", "ログイン中の従業員を削除することは出来ません")));
            // 同一日付チェック用エラーメッセージ
            put(ErrorKinds.DATECHECK_ERROR, new ArrayList<String>(Arrays.asList("reportDateError", "既に登録されている日付です")));
            // 日報日付空白チェック用エラーメッセージ
            put(ErrorKinds.DATECHECK_BLANK_ERROR, new ArrayList<String>(Arrays.asList("reportDateBlankError", "値を入力してください")));
           // 日報タイトル空白チェック用エラーメッセージ
            put(ErrorKinds.TITLECHECK_BLANK_ERROR, new ArrayList<String>(Arrays.asList("reportTitleBlankError", "値を入力してください")));
            // 日報タイトル100文字桁数チェック用エラーメッセージ
            put(ErrorKinds.TITLECHECK_RANGE_ERROR, new ArrayList<String>(Arrays.asList("reportTitleRangeError", "100文字以下で入力してください")));
            // 日報内容空白チェック用エラーメッセージ
            put(ErrorKinds.CONTENTCHECK_BLANK_ERROR, new ArrayList<String>(Arrays.asList("reportContentBlankError", "値を入力してください")));
            // 日報内容600文字桁数チェック用エラーメッセージ
            put(ErrorKinds.CONTENTCHECK_RANGE_ERROR, new ArrayList<String>(Arrays.asList("reportContentRangekError", "600文字以下で入力してください")));
         // 日報新規作成で 日付重複エラー用エラーメッセージ
            put(ErrorKinds.DUPLICATE_DATE_ERROR, new ArrayList<String>(Arrays.asList("reportDateCheckError", "aaaaa既に登録されている日付です")));



        }
    };

    // エラーメッセージマップにあるエラーかどうかのチェック
    public static boolean contains(ErrorKinds errorKinds) {
        if (errorMessageMap.containsKey(errorKinds)) {
            return true;
        } else {
            return false;
        }
    }

    // エラーメッセージの名称を取得
    public static String getErrorName(ErrorKinds errorKinds) {
        return errorMessageMap.get(errorKinds).get(0);
    }

    // エラーメッセージの値を取得
    public static String getErrorValue(ErrorKinds errorKinds) {
        return errorMessageMap.get(errorKinds).get(1);
    }
}
