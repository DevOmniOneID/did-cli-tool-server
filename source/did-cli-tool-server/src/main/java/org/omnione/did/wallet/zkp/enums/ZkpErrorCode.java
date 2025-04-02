package org.omnione.did.wallet.zkp.enums;

public enum ZkpErrorCode {

    // for IWErrorCode
    OMNI_ERROR_ZKP_FAIL                                                    (1, "failure"),

    // for common
    OMNI_ERROR_ZKP_UNKNOWN                                                 (-1,"unknown"),
    OMNI_ERROR_ZKP_SUCCESS                                                 (0, "succeed"),
    OMNI_ERROR_ZKP_PARAMETER_VALID_FAIL                                    (-10001,"parameter valid fail"),
    OMNI_ERROR_ZKP_GENERATE_NONCE_FAIL                                     (-10002,"generate nonce fail"),
    OMNI_ERROR_ZKP_BIG_NUMBER_CALCULATE_FAIL                               (-10003,"big number calculate fail"),
    OMNI_ERROR_ZKP_BIG_NUMBER_TO_BYTE_FAIL                                 (-10004,"big number from byte fail"),
    OMNI_ERROR_ZKP_BIG_NUMBER_TO_HEXA_STRING_FAIL                          (-10005,"big number from hexa string fail"),
    OMNI_ERROR_ZKP_BIG_NUMBER_FROM_BYTE_FAIL                               (-10006,"big number from json fail"),
    OMNI_ERROR_ZKP_BIG_NUMBER_FROM_JSON_FAIL                               (-10007,"big number hexa string fail"),
    OMNI_ERROR_ZKP_BIG_NUMBER_FROM_HEXA_STRING_FAIL                        (-10008,"big number from hexa string fail"),
    OMNI_ERROR_ZKP_BIG_NUMBER_COMPARE_FAIL                                 (-10009,"big number compare fail"),
    OMNI_ERROR_ZKP_NO_DATA_IN_WALLET                                       (-10010,"no data in wallet"),
    OMNI_ERROR_ZKP_APPEND_BIG_NUMBER_FOR_HASH_FAIL                         (-10011,"append big number for hash fail"),
    OMNI_ERROR_ZKP_APPEND_BINARY_FOR_HASH_FAIL                             (-10012,"append binary for hash fail"),
    OMNI_ERROR_ZKP_FINAL_FOR_HASH_FAIL                                     (-10013,"final for hash fail"),
    OMNI_ERROR_ZKP_GET_TAIL_FROM_TAILS_FILE_FAIL                           (-10014,"get tail from tails file fail"),
    OMNI_ERROR_ZKP_NOT_FOUND_TAILS_FILE                                    (-10015,"not found tails file"),
    OMNI_ERROR_ZKP_BIG_NUMBER_IS_NOT_PRIME                                 (-10016, "big number is not prime"),
    OMNI_ERROR_ZKP_GROUP_ORDER_TO_BIG_NUMBER_FAIL                          (-10017, "group order to big number failed"),
    OMNI_ERROR_ZKP_BIG_NUMBER_TO_GROUP_ORDER_FAIL                          (-10018, "big number to group order failed"),
    OMNI_ERROR_ZKP_NON_REVOC_AS_TAU_LIST_FAIL                              (-10019, "non revocation as tau list failed"),
    OMNI_ERROR_ZKP_NON_REVOC_AS_C_LIST_FAIL                                (-10020, "non revocation as c list failed"),
    OMNI_ERROR_ZKP_CALCULATE_TEQ_FAIL                                      (-10021, "calculate TEQ failed"),
    OMNI_ERROR_ZKP_CALCULATE_TNE_FAIL                                      (-10022, "calculate TNE failed"),
    // for java
    OMNI_ERROR_ZKP_IO                                                      (-10050,"error io"),
    OMNI_ERROR_ZKP_NO_SUCH_ALG                                             (-10051,"no such algorithm"),
    OMNI_ERROR_ZKP_NULL                                                    (-10052,"null"),
    OMNI_ERROR_ZKP_DUPLICATED                                              (-10053,"duplicated key"),
    OMNI_ERROR_ZKP_NOT_SUPPORTED_TYPE                                      (-10054,"not supported type"),
    OMNI_ERROR_ZKP_NOT_SUPPORTED_PREDICATE_TYPE                            (-10055,"not supported predicate type"),

    // for issuer
    OMNI_ERROR_ZKP_ISSUER_GENERATE_PR_PRIVATE_KEY_FAIL                     (-11001,"generate pr private key fail"),
    OMNI_ERROR_ZKP_ISSUER_GENERATE_PR_PUBLIC_KEY_FAIL                      (-11002,"generate pr public key fail"),
    OMNI_ERROR_ZKP_ISSUER_GENERATE_PR_KEY_METADATA_FAIL                    (-11003,"generate pr key metadata fail"),
    OMNI_ERROR_ZKP_ISSUER_GENERATE_CRED_PRIMARY_KEY_FAIL                   (-11004,"generate credential primary key fail"),
    OMNI_ERROR_ZKP_ISSUER_GENERATE_CRED_KEY_CORRECTNESS_PROOF_FAIL         (-11005,"generate credential key correctness proof fail"),
    OMNI_ERROR_ZKP_ISSUER_GENERATE_CRED_REVOCATION_KEY_FAIL                (-11006,"generate credential revocation key fail"),
    OMNI_ERROR_ZKP_ISSUER_INSERT_CRED_DEF_DATA_TO_WALLET_FAIL              (-11007,"generate credential definition data to wallet fail"),
    OMNI_ERROR_ZKP_ISSUER_SELECT_CRED_DEF_DATA_FROM_WALLET_FAIL            (-11008,"generate credential definition data from wallet fail"),
    OMNI_ERROR_ZKP_ISSUER_GENERATE_REVOCATION_KEY_FAIL                     (-11009,"generate revocation key fail"),
    OMNI_ERROR_ZKP_ISSUER_GENERATE_REVOCATION_REGISTRY_FAIL                (-11010,"generate revocation registry fail"),
    OMNI_ERROR_ZKP_ISSUER_CREATE_AND_STORE_TAILS_FAIL                      (-11011,"create and store tails fail"),
    OMNI_ERROR_ZKP_ISSUER_SELECT_REV_REG_DATA_FROM_WALLET_FAIL             (-11012,"select revocation registry data from wallet"),
    OMNI_ERROR_ZKP_ISSUER_ISSUED_CRED_NUMBER_OVER                          (-11013,"issued credential number over"),
    OMNI_ERROR_ZKP_ISSUER_NO_COMPARE_CRED_DEF_ID                           (-11014,"no compare credential definition id"),
    OMNI_ERROR_ZKP_ISSUER_CHECK_BLINDED_SECRETS_CORRECTNESS_PROOF_FAIL     (-11015,"check blinded secrets correctness proof fail"),
    OMNI_ERROR_ZKP_ISSUER_GET_AVAILABLE_REVOCATION_REGISTRY_FAIL           (-11016,"get available revocation registry fail"),
    OMNI_ERROR_ZKP_ISSUER_GENERATE_CREDENTIAL_CONTEXT_FAIL                 (-11017,"generate credential context fail"),
    OMNI_ERROR_ZKP_ISSUER_GENERATE_PRIMARY_CREDENTIAL_FAIL                 (-11018,"generate primary credential fail"),
    OMNI_ERROR_ZKP_ISSUER_GENERATE_SIGNATURE_CORRECTNESS_PROOF_FAIL        (-11019,"generate signature correctness proof fail"),
    OMNI_ERROR_ZKP_ISSUER_GENERATE_REVOCATION_CREDENTIAL_FAIL              (-11020,"generate revocation credential fail"),
    OMNI_ERROR_ZKP_ISSUER_GENERATE_WITNESS_FAIL                            (-11021,"generate witness fail"),
    OMNI_ERROR_ZKP_ISSUER_GENERATE_CREDENTIAL_ID_FAIL                      (-11022,"generate credential id fail"),
    OMNI_ERROR_ZKP_ISSUER_UPDATE_REV_REG_DATA_TO_WALLET_FAIL               (-11023, "update revocation registry filed"),
    // for java
    OMNI_ERROR_ZKP_ISSUER_NO_COMPARE_REV_REG_DEF_ID                        (-11050,"no compare revocation credential definition id"),
    OMNI_ERROR_ZKP_ISSUER_NOT_SATISFIED_CREDENTIAL_NUMBER                  (-11051,"at least greater then maxCredNum 0"),
    OMNI_ERROR_ZKP_ISSUER_NOT_CONTAIN_REVOCATION_INDEX_IN_USED_ID          (-11052,"not contain revocation index in usedId"),

    // for prover
    OMNI_ERROR_ZKP_PROVER_GENERATE_MASTER_SECRET_FAIL                      (-12001,"generate master secret fail"),
    OMNI_ERROR_ZKP_PROVER_CHECK_CREDENTIAL_KEY_CORRECTNESS_PROOF_FAIL      (-12002,"check credential key correctness proof fail"),
    OMNI_ERROR_ZKP_PROVER_NEW_BLINDED_PRIMARY_CRED_SECRETS_FACTORS_FAIL    (-12003,"new blinded primary credential secrets factors fail"),
    OMNI_ERROR_ZKP_PROVER_NEW_BLINDED_CRED_SECRETS_CORRECTNESS_PROOF_FAIL  (-12004,"new blinded credential secrets correctness proof fail"),
    OMNI_ERROR_ZKP_PROVER_RAW_TO_ENCODED_FAIL                              (-12005,"raw to encoded fail"),
    OMNI_ERROR_ZKP_PROVER_SELECT_MASTER_SECRET_FROM_WALLET_FAIL            (-12006,"select master secret from wallet fail"),
    OMNI_ERROR_ZKP_PROVER_CHECK_SIGNATURE_CORRECTNESS_PROOF_FAIL           (-12007,"check signature correctness proof fail"),
    OMNI_ERROR_ZKP_PROVER_CHECK_WITNESS_SIGNATURE_FAIL                     (-12008,"check witness signature fail"),
    OMNI_ERROR_ZKP_PROVER_INSERT_CREDENTIAL_TO_WALLET_FAIL                 (-12009,"insert credential to wallet fail"),
    OMNI_ERROR_ZKP_PROVER_SELECT_CREDENTIAL_FROM_WALLET_FAIL               (-12010,"select credential from wallet fail"),
    OMNI_ERROR_ZKP_PROVER_NOT_FOUND_AVAILABLE_REQUEST_ATTRIBUTE            (-12011,"not found available request attribute"),
    OMNI_ERROR_ZKP_PROVER_NOT_FOUND_AVAILABLE_PREDICATE_ATTRIBUTE          (-12012,"not found available predicate attribute"),
    OMNI_ERROR_ZKP_PROVER_BUILD_CREDENTIAL_FOR_PROVING_FAIL                (-12013,"build credential for proving fail"),
    OMNI_ERROR_ZKP_PROVER_NOT_FOUND_SCHEMA_FROM_LIST                       (-12014,"not found schema from list"),
    OMNI_ERROR_ZKP_PROVER_NOT_FOUND_CRED_DEF_FROM_LIST                     (-12015,"not found credential definition from list"),
    OMNI_ERROR_ZKP_PROVER_NOT_FOUND_REV_REG_STATE_FROM_LIST                (-12016,"not found revocation registry state from list"),
    OMNI_ERROR_ZKP_PROVER_INITIALIZE_PRIMARY_EQ_PROOF_FAIL                 (-12017,"initialize primary equal proof fail"),
    OMNI_ERROR_ZKP_PROVER_INITIALIZE_PRIMARY_NE_PROOF_FAIL                 (-12018,"initialize primary non equal proof fail"),
    OMNI_ERROR_ZKP_PROVER_INITIALIZE_PRIMARY_PROOF_FAIL                    (-12019,"initialize primary proof fail"),
    OMNI_ERROR_ZKP_PROVER_FINALIZE_PRIMARY_EQ_PROOF_FAIL                   (-12020,"finalize primary equal proof fail"),
    OMNI_ERROR_ZKP_PROVER_FINALIZE_PRIMARY_NE_PROOF_FAIL                   (-12021,"finalize primary non equal proof fail"),
    OMNI_ERROR_ZKP_PROVER_FINALIZE_PRIMARY_PROOF_FAIL                      (-12022,"finalize primary proof fail"),
    OMNI_ERROR_ZKP_PROVER_INITIALIZE_REVOCATION_PROOF_FAIL                 (-12023,"initialize revocation proof fail"),
    // for prover (user input) // 유저 액션으로 발생하는 에러 정의


    // for verifier
    OMNI_ERROR_ZKP_VERIFIER_DUPLICATE_RESTRICTION                          (-13001,"duplicate restriction"),
    OMNI_ERROR_ZKP_VERIFIER_VERIFY_NON_REVOCATION_PROOF_FAIL               (-13002,"verify non revocation proof fail"),
    OMNI_ERROR_ZKP_VERIFIER_VERIFY_PRIMARY_EQ_PROOF_FAIL                   (-13003,"verify primary equal proof fail"),
    OMNI_ERROR_ZKP_VERIFIER_VERIFY_PRIMARY_NE_PROOF_FAIL                   (-13004,"verify primary non equal proof fail"),
    OMNI_ERROR_ZKP_VERIFIER_VERIFY_PRIMARY_PROOF_FAIL                      (-13005,"verify primary proof fail"),
    OMNI_ERROR_ZKP_VERIFIER_NOT_SATISFIED_ATTRIBUTE_IN_SUB_PROOF           (-13006,"not satisfied attribute in sub proof");

    private int errCode;
    private String message;

    ZkpErrorCode(int errCode) {
        this.errCode = errCode;
    }
    ZkpErrorCode(int errCode, String message) {
        this.errCode = errCode;
        this.message = message;
    }

    ZkpErrorCode(int errCode, int addCode, String message) {
        this.errCode = errCode;
        this.errCode = errCode + addCode;
        this.message = message;
    }

    public int getCode() {
        return this.errCode;
    }
    public String getMessage() {
        return this.message;
    }

    public void setMessage(String msg) {
        this.message = msg;
    }
}
