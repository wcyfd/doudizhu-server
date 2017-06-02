// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: Error.proto

package com.randioo.doudizhu_server.protocol;

public final class Error {
  private Error() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public enum ErrorCode
      implements com.google.protobuf.ProtocolMessageEnum {
    OK(0, 1),
    NO_STRING(1, 2),
    NO_ROLE_ACCOUNT(2, 3),
    EXIST_ROLE(3, 4),
    CREATE_FAILED(4, 5),
    REJECT_LOGIN(5, 6),
    ACCOUNT_ILLEGEL(6, 7),
    IN_LOGIN(7, 8),
    NAME_SENSITIVE(8, 9),
    NO_ROLE_DATA(9, 10),
    NAME_REPEATED(10, 11),
    NAME_TOO_LONG(11, 12),
    NAME_SPECIAL_CHAR(12, 13),
    NO_MONEY(13, 14),
    CONNECT_ERROR(14, 15),
    GAME_CREATE_ERROR(15, 16),
    GAME_JOIN_ERROR(16, 17),
    ROUND_ERROR(17, 18),
    MONEY_NUM_ERROR(18, 19),
    MATCH_ERROR_LOCK(19, 20),
    MATCH_MAX_ROLE_COUNT(20, 21),
    GAME_NOT_EXIST(21, 22),
    GAME_EXITING(22, 23),
    APPLY_REJECT(23, 24),
    ;
    
    
    public final int getNumber() { return value; }
    
    public static ErrorCode valueOf(int value) {
      switch (value) {
        case 1: return OK;
        case 2: return NO_STRING;
        case 3: return NO_ROLE_ACCOUNT;
        case 4: return EXIST_ROLE;
        case 5: return CREATE_FAILED;
        case 6: return REJECT_LOGIN;
        case 7: return ACCOUNT_ILLEGEL;
        case 8: return IN_LOGIN;
        case 9: return NAME_SENSITIVE;
        case 10: return NO_ROLE_DATA;
        case 11: return NAME_REPEATED;
        case 12: return NAME_TOO_LONG;
        case 13: return NAME_SPECIAL_CHAR;
        case 14: return NO_MONEY;
        case 15: return CONNECT_ERROR;
        case 16: return GAME_CREATE_ERROR;
        case 17: return GAME_JOIN_ERROR;
        case 18: return ROUND_ERROR;
        case 19: return MONEY_NUM_ERROR;
        case 20: return MATCH_ERROR_LOCK;
        case 21: return MATCH_MAX_ROLE_COUNT;
        case 22: return GAME_NOT_EXIST;
        case 23: return GAME_EXITING;
        case 24: return APPLY_REJECT;
        default: return null;
      }
    }
    
    public static com.google.protobuf.Internal.EnumLiteMap<ErrorCode>
        internalGetValueMap() {
      return internalValueMap;
    }
    private static com.google.protobuf.Internal.EnumLiteMap<ErrorCode>
        internalValueMap =
          new com.google.protobuf.Internal.EnumLiteMap<ErrorCode>() {
            public ErrorCode findValueByNumber(int number) {
              return ErrorCode.valueOf(number)
    ;        }
          };
    
    public final com.google.protobuf.Descriptors.EnumValueDescriptor
        getValueDescriptor() {
      return getDescriptor().getValues().get(index);
    }
    public final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptorForType() {
      return getDescriptor();
    }
    public static final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptor() {
      return com.randioo.doudizhu_server.protocol.Error.getDescriptor().getEnumTypes().get(0);
    }
    
    private static final ErrorCode[] VALUES = {
      OK, NO_STRING, NO_ROLE_ACCOUNT, EXIST_ROLE, CREATE_FAILED, REJECT_LOGIN, ACCOUNT_ILLEGEL, IN_LOGIN, NAME_SENSITIVE, NO_ROLE_DATA, NAME_REPEATED, NAME_TOO_LONG, NAME_SPECIAL_CHAR, NO_MONEY, CONNECT_ERROR, GAME_CREATE_ERROR, GAME_JOIN_ERROR, ROUND_ERROR, MONEY_NUM_ERROR, MATCH_ERROR_LOCK, MATCH_MAX_ROLE_COUNT, GAME_NOT_EXIST, GAME_EXITING, APPLY_REJECT, 
    };
    public static ErrorCode valueOf(
        com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new java.lang.IllegalArgumentException(
          "EnumValueDescriptor is not for this type.");
      }
      return VALUES[desc.getIndex()];
    }
    private final int index;
    private final int value;
    private ErrorCode(int index, int value) {
      this.index = index;
      this.value = value;
    }
    
    static {
      com.randioo.doudizhu_server.protocol.Error.getDescriptor();
    }
    
    // @@protoc_insertion_point(enum_scope:com.randioo.doudizhu_server.protocol.ErrorCode)
  }
  
  
  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\013Error.proto\022$com.randioo.doudizhu_serv" +
      "er.protocol*\315\003\n\tErrorCode\022\006\n\002OK\020\001\022\r\n\tNO_" +
      "STRING\020\002\022\023\n\017NO_ROLE_ACCOUNT\020\003\022\016\n\nEXIST_R" +
      "OLE\020\004\022\021\n\rCREATE_FAILED\020\005\022\020\n\014REJECT_LOGIN" +
      "\020\006\022\023\n\017ACCOUNT_ILLEGEL\020\007\022\014\n\010IN_LOGIN\020\010\022\022\n" +
      "\016NAME_SENSITIVE\020\t\022\020\n\014NO_ROLE_DATA\020\n\022\021\n\rN" +
      "AME_REPEATED\020\013\022\021\n\rNAME_TOO_LONG\020\014\022\025\n\021NAM" +
      "E_SPECIAL_CHAR\020\r\022\014\n\010NO_MONEY\020\016\022\021\n\rCONNEC" +
      "T_ERROR\020\017\022\025\n\021GAME_CREATE_ERROR\020\020\022\023\n\017GAME" +
      "_JOIN_ERROR\020\021\022\017\n\013ROUND_ERROR\020\022\022\023\n\017MONEY_",
      "NUM_ERROR\020\023\022\024\n\020MATCH_ERROR_LOCK\020\024\022\030\n\024MAT" +
      "CH_MAX_ROLE_COUNT\020\025\022\022\n\016GAME_NOT_EXIST\020\026\022" +
      "\020\n\014GAME_EXITING\020\027\022\020\n\014APPLY_REJECT\020\030"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          return null;
        }
      };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
  }
  
  public static void internalForceInit() {}
  
  // @@protoc_insertion_point(outer_class_scope)
}
