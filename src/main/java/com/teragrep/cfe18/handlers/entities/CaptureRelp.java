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

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CaptureRelp {
    public enum CaptureType {
        cfe, relp
    }

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Integer id;
    private String tag;
    private String application;
    private String index;
    private String retentionTime;
    private String sourceType;
    private String category;
    private String flow;
    private String protocol;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private CaptureType type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getRetention_time() {
        return retentionTime;
    }

    public void setRetention_time(String retention_time) {
        this.retentionTime = retention_time;
    }

    public String getSource_type() {
        return sourceType;
    }

    public void setSource_type(String source_type) {
        this.sourceType = source_type;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFlow() {
        return flow;
    }

    public void setFlow(String flow) {
        this.flow = flow;
    }

    public CaptureType getCaptureType() {
        return type;
    }

    public void setCaptureType(CaptureType captureType) {
        this.type = captureType;
    }

    @Override
    public String toString() {
        return "CaptureRelp{" +
                "id=" + id +
                ", tag='" + tag + '\'' +
                ", retention_time='" + retentionTime + '\'' +
                ", category='" + category + '\'' +
                ", application='" + application + '\'' +
                ", index='" + index + '\'' +
                ", source_type='" + sourceType + '\'' +
                ", protocol='" + protocol + '\'' +
                ", flow='" + flow + '\'' +
                ", captureType=" + type +
                '}';
    }
}
