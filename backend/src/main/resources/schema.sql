DROP TABLE IF EXISTS team_assignment;
DROP TABLE IF EXISTS team_member;
DROP TABLE IF EXISTS watchlist_item;
DROP TABLE IF EXISTS push_delivery_log;
DROP TABLE IF EXISTS push_channel_config;
DROP TABLE IF EXISTS brand_selection_model;
DROP TABLE IF EXISTS competitor_timeline_point;
DROP TABLE IF EXISTS competitor_shop;
DROP TABLE IF EXISTS opportunity;
DROP TABLE IF EXISTS insight_card;
DROP TABLE IF EXISTS supply_demand;
DROP TABLE IF EXISTS competition_data;
DROP TABLE IF EXISTS category_trend;
DROP TABLE IF EXISTS action_status;
DROP TABLE IF EXISTS brand_info;

CREATE TABLE brand_info (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  brand_name VARCHAR(100) NOT NULL,
  industry VARCHAR(80) NOT NULL,
  target_category VARCHAR(100),
  has_category BOOLEAN NOT NULL,
  interest_direction VARCHAR(200),
  target_platforms VARCHAR(200) NOT NULL,
  budget_range VARCHAR(50),
  profit_min VARCHAR(50),
  supply_chain VARCHAR(500),
  stock_cycle VARCHAR(50),
  exclude_categories VARCHAR(300),
  existing_products VARCHAR(500),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE category_trend (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  category_name VARCHAR(100) NOT NULL,
  platform VARCHAR(40) NOT NULL,
  trend_month VARCHAR(20) NOT NULL,
  search_volume INT NOT NULL,
  sales_volume INT NOT NULL,
  growth_rate DECIMAL(8, 2) NOT NULL,
  social_heat INT NOT NULL,
  rising_words VARCHAR(300)
);

CREATE TABLE competition_data (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  category_name VARCHAR(100) NOT NULL,
  platform VARCHAR(40) NOT NULL,
  total_search_volume INT NOT NULL,
  total_sku_count INT NOT NULL,
  top10_sales_ratio DECIMAL(8, 2) NOT NULL,
  cr3 DECIMAL(8, 2) NOT NULL,
  cr5 DECIMAL(8, 2) NOT NULL,
  homogeneity_score DECIMAL(8, 2) NOT NULL,
  conclusion VARCHAR(500)
);

CREATE TABLE supply_demand (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  category_name VARCHAR(100) NOT NULL,
  platform VARCHAR(40) NOT NULL,
  price_range VARCHAR(50) NOT NULL,
  search_volume INT NOT NULL,
  supply_count INT NOT NULL,
  demand_supply_ratio DECIMAL(8, 2) NOT NULL
);

CREATE TABLE insight_card (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  brand_id BIGINT NOT NULL DEFAULT 1,
  category_name VARCHAR(100) NOT NULL,
  market_size VARCHAR(80) NOT NULL,
  market_growth VARCHAR(50) NOT NULL,
  competition_pattern VARCHAR(120) NOT NULL,
  competition_level VARCHAR(40) NOT NULL,
  price_gap VARCHAR(120) NOT NULL,
  estimated_startup_cost VARCHAR(80) NOT NULL,
  recommendation VARCHAR(500) NOT NULL
);

CREATE TABLE opportunity (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  insight_card_id BIGINT NOT NULL,
  category_name VARCHAR(100) NOT NULL,
  opportunity_gravity DECIMAL(8, 2) NOT NULL,
  competition_resistance DECIMAL(8, 2) NOT NULL,
  profit_elasticity DECIMAL(8, 2) NOT NULL,
  opportunity_score INT NOT NULL,
  opportunity_level VARCHAR(20) NOT NULL,
  target_crowd VARCHAR(120) NOT NULL,
  scenario_text VARCHAR(160) NOT NULL,
  differentiation VARCHAR(240) NOT NULL,
  market_estimate VARCHAR(120) NOT NULL,
  entry_timing VARCHAR(160) NOT NULL,
  lifecycle_stage VARCHAR(40) NOT NULL,
  decision VARCHAR(40) NOT NULL,
  reason VARCHAR(500) NOT NULL
);

CREATE TABLE competitor_timeline_point (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  shop_name VARCHAR(120) NOT NULL,
  platform VARCHAR(40) NOT NULL,
  focus_category VARCHAR(100) NOT NULL,
  week_order INT NOT NULL,
  week_label VARCHAR(20) NOT NULL,
  heat_index INT NOT NULL,
  sales_index INT NOT NULL,
  note VARCHAR(300) NOT NULL,
  UNIQUE (shop_name, platform, focus_category, week_order)
);

CREATE TABLE competitor_shop (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  brand_id BIGINT NOT NULL,
  shop_name VARCHAR(120) NOT NULL,
  platform VARCHAR(40) NOT NULL,
  focus_category VARCHAR(100) NOT NULL,
  latest_hit VARCHAR(300) NOT NULL,
  growth_signal VARCHAR(300) NOT NULL,
  added_at VARCHAR(30) NOT NULL,
  card_id BIGINT,
  source_signal_id VARCHAR(80),
  source_signal_type VARCHAR(80),
  recent_launch VARCHAR(300),
  hit_product_count INT NOT NULL,
  complaint_topics VARCHAR(500),
  opportunity_tags VARCHAR(500),
  builtin_seed BOOLEAN NOT NULL DEFAULT FALSE,
  UNIQUE (brand_id, shop_name, platform, focus_category)
);

CREATE TABLE action_status (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  insight_card_id BIGINT NOT NULL,
  action_title VARCHAR(200) NOT NULL,
  status VARCHAR(40) NOT NULL,
  note VARCHAR(500),
  updated_at VARCHAR(30) NOT NULL,
  UNIQUE (insight_card_id, action_title)
);

CREATE TABLE watchlist_item (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  brand_id BIGINT NOT NULL,
  card_id BIGINT,
  category_name VARCHAR(100) NOT NULL,
  note VARCHAR(300),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (brand_id, category_name, card_id)
);

CREATE TABLE push_channel_config (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  brand_id BIGINT NOT NULL,
  channel_type VARCHAR(40) NOT NULL,
  webhook_url VARCHAR(500) NOT NULL,
  enabled BOOLEAN NOT NULL DEFAULT TRUE,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (brand_id, channel_type)
);

CREATE TABLE push_delivery_log (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  brand_id BIGINT NOT NULL,
  channel_type VARCHAR(40) NOT NULL,
  status VARCHAR(20) NOT NULL,
  webhook_masked VARCHAR(120) NOT NULL,
  payload_preview VARCHAR(500) NOT NULL,
  response_body VARCHAR(500),
  delivered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE brand_selection_model (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  brand_id BIGINT NOT NULL UNIQUE,
  trend_weight DECIMAL(5, 2) NOT NULL,
  competition_weight DECIMAL(5, 2) NOT NULL,
  supply_gap_weight DECIMAL(5, 2) NOT NULL,
  brand_fit_weight DECIMAL(5, 2) NOT NULL,
  risk_penalty_weight DECIMAL(5, 2) NOT NULL,
  model_version VARCHAR(40) NOT NULL,
  training_summary VARCHAR(500) NOT NULL,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE team_member (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  brand_id BIGINT NOT NULL,
  member_name VARCHAR(80) NOT NULL,
  role_label VARCHAR(40) NOT NULL,
  permission_level VARCHAR(20) NOT NULL DEFAULT 'editor',
  account_id VARCHAR(64),
  email VARCHAR(120),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (brand_id, account_id)
);

CREATE TABLE team_assignment (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  brand_id BIGINT NOT NULL,
  card_id BIGINT,
  action_title VARCHAR(200) NOT NULL,
  assignee_name VARCHAR(80) NOT NULL,
  status VARCHAR(40) NOT NULL,
  approval_status VARCHAR(20) NOT NULL DEFAULT 'pending',
  approver_name VARCHAR(80),
  note VARCHAR(500),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
