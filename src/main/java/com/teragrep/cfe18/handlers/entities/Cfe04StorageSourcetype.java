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

public class Cfe04StorageSourcetype {

    private int sourcetypeId;
    private String maxDaysAgo;
    private String category;
    private String sourceDescription;
    private String truncate;
    private boolean freeformIndexerEnabled;
    private String freeformIndexerText;
    private boolean freeformLbEnabled;
    private String freeformLbText;

    public int getSourcetypeId() {
        return sourcetypeId;
    }

    public void setSourcetypeId(int sourcetypeId) {
        this.sourcetypeId = sourcetypeId;
    }

    public String getMaxDaysAgo() {
        return maxDaysAgo;
    }

    public void setMaxDaysAgo(String maxDaysAgo) {
        this.maxDaysAgo = maxDaysAgo;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSourceDescription() {
        return sourceDescription;
    }

    public void setSourceDescription(String sourceDescription) {
        this.sourceDescription = sourceDescription;
    }

    public String getTruncate() {
        return truncate;
    }

    public void setTruncate(String truncate) {
        this.truncate = truncate;
    }

    public boolean isFreeformIndexerEnabled() {
        return freeformIndexerEnabled;
    }

    public void setFreeformIndexerEnabled(boolean freeformIndexerEnabled) {
        this.freeformIndexerEnabled = freeformIndexerEnabled;
    }

    public String getFreeformIndexerText() {
        return freeformIndexerText;
    }

    public void setFreeformIndexerText(String freeformIndexerText) {
        this.freeformIndexerText = freeformIndexerText;
    }

    public boolean isFreeformLbEnabled() {
        return freeformLbEnabled;
    }

    public void setFreeformLbEnabled(boolean freeformLbEnabled) {
        this.freeformLbEnabled = freeformLbEnabled;
    }

    public String getFreeformLbText() {
        return freeformLbText;
    }

    public void setFreeformLbText(String freeformLbText) {
        this.freeformLbText = freeformLbText;
    }

    @Override
    public String toString() {
        return "Cfe04StorageSourcetype{" + "sourcetypeId=" + sourcetypeId + ", maxDaysAgo='" + maxDaysAgo + '\''
                + ", category='" + category + '\'' + ", sourceDescription='" + sourceDescription + '\'' + ", truncate='"
                + truncate + '\'' + ", freeformIndexerEnabled=" + freeformIndexerEnabled + ", freeformIndexerText='"
                + freeformIndexerText + '\'' + ", freeformLbEnabled=" + freeformLbEnabled + ", freeformLbText='"
                + freeformLbText + '\'' + '}';
    }
}
