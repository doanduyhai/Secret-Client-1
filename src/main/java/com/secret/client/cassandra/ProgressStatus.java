package com.secret.client.cassandra;

public enum ProgressStatus {
    CLIENT_IMPORTED,CLIENT_VALIDATED,ODM_RES,EXPORT_BI;

    @Override
    public String toString() {
        return this.name();
    }


}
