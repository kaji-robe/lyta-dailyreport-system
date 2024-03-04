package com.techacademy.constants;

// エラーメッセージ定義
public enum ErrorKinds {

    // エラー内容
    // 空白チェックエラー
    BLANK_ERROR,
    // 半角英数字チェックエラー
    HALFSIZE_ERROR,
    // 桁数(8桁~16桁以外)チェックエラー
    RANGECHECK_ERROR,
    // 重複チェックエラー(例外あり)
    DUPLICATE_EXCEPTION_ERROR,
    // 重複チェックエラー(例外なし)
    DUPLICATE_ERROR,
    // ログイン中削除チェックエラー
    LOGINCHECK_ERROR,

// 日付チェックエラー
    DATECHECK_ERROR,
// 日報空白日付チェックエラー
    DATECHECK_BLANK_ERROR,
// 日報タイトル空白チェックエラー
    TITLECHECK_BLANK_ERROR,
// 日報タイトル桁数チェックエラー
    TITLECHECK_RANGE_ERROR,
// 日報内容空白チェックエラー
    CONTENTCHECK_BLANK_ERROR,
// 日報内容桁数チェックエラー
    CONTENTCHECK_RANGE_ERROR,
 // 日報日付重複エラー
    DUPLICATE_DATE_ERROR,


    // チェックOK
    CHECK_OK,
    // 正常終了
    SUCCESS, UNKNOWN_ERROR, NOT_FOUND, NOT_FOUND_ERROR;

}
