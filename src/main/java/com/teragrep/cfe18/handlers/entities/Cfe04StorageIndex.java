/*
 * Integration main data management for Teragrep
 * Copyright (C) 2021  Suomen Kanuuna Oy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://github.com/teragrep/teragrep/blob/main/LICENSE>.
 *
 *
 * Additional permission under GNU Affero General Public License version 3
 * section 7
 *
 * If you modify this Program, or any covered work, by linking or combining it
 * with other code, such other code is not for that reason alone subject to any
 * of the requirements of the GNU Affero GPL version 3 as long as this Program
 * is the same Program as licensed from Suomen Kanuuna Oy without any additional
 * modifications.
 *
 * Supplemented terms under GNU Affero General Public License version 3
 * section 7
 *
 * Origin of the software must be attributed to Suomen Kanuuna Oy. Any modified
 * versions must be marked as "Modified version of" The Program.
 *
 * Names of the licensors and authors may not be used for publicity purposes.
 *
 * No rights are granted for use of trade names, trademarks, or service marks
 * which are in The Program if any.
 *
 * Licensee must indemnify licensors and authors for any liability that these
 * contractual assumptions impose on licensors and authors.
 *
 * To the extent this program is licensed as part of the Commercial versions of
 * Teragrep, the applicable Commercial License may apply to this file if you as
 * a licensee so wish it.
 */
package com.teragrep.cfe18.handlers.entities;

public class Cfe04StorageIndex {

    private int indexId;
    private String repFactor;
    private boolean disabled;
    private String homePath;
    private String coldpath;
    private String thawedPath;

    public int getIndexId() {
        return indexId;
    }

    public void setIndexId(int indexId) {
        this.indexId = indexId;
    }

    public String getRepFactor() {
        return repFactor;
    }

    public void setRepFactor(String repFactor) {
        this.repFactor = repFactor;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getHomePath() {
        return homePath;
    }

    public void setHomePath(String homePath) {
        this.homePath = homePath;
    }

    public String getColdpath() {
        return coldpath;
    }

    public void setColdpath(String coldpath) {
        this.coldpath = coldpath;
    }

    public String getThawedPath() {
        return thawedPath;
    }

    public void setThawedPath(String thawedPath) {
        this.thawedPath = thawedPath;
    }

    @Override
    public String toString() {
        return "Cfe04StorageIndex{" + "indexId=" + indexId + ", repFactor='" + repFactor + '\'' + ", disabled="
                + disabled + ", homePath='" + homePath + '\'' + ", coldpath='" + coldpath + '\'' + ", thawedPath='"
                + thawedPath + '\'' + '}';
    }
}
