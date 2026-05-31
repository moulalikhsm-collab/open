import streamlit as st
import random
import time
import pandas as pd
import numpy as np

# ==============================================================================
# PAGE CONFIGURATION & THEME STYLING
# ==============================================================================
st.set_page_config(
    page_title="EcoFriend – Growing a Greener Future with AI",
    page_icon="🌱",
    layout="wide",
    initial_sidebar_state="expanded"
)

# Native-Inspired Custom Premium Palette Style Sheets
st.markdown("""
<style>
    /* Global Background & Font Setup */
    .stApp {
        background-color: #F3F5F1;
        color: #1E293B;
    }
    h1, h2, h3, h4, h5, h6 {
        font-family: 'Playfair Display', 'Georgia', serif !important;
        color: #1B5E20 !important;
        font-weight: 800;
    }
    /* Force high visibility of text/labels on the light background while preserving dark components */
    p, span, label, li, caption, small {
        font-family: 'Inter', 'Segoe UI', sans-serif;
    }
    
    /* Ensure the accented header card with dark green background maintains high-contrast white text */
    .accent-header-card, 
    .accent-header-card p, 
    .accent-header-card span, 
    .accent-header-card h1, 
    .accent-header-card h2, 
    .accent-header-card h3, 
    .accent-header-card h4 {
        color: #FFFFFF !important;
    }

    /* Specialized dynamic styling for the pipeline command simulator terminal */
    .eco-terminal, .eco-terminal p, .eco-terminal span, .eco-terminal b {
        font-family: 'Courier New', Courier, monospace !important;
    }
    .eco-terminal .term-comment {
        color: #A3E635 !important;
        font-weight: bold;
    }
    .eco-terminal .term-waiting {
        color: #38BDF8 !important;
        font-weight: bold;
    }
    .eco-terminal .term-step1 {
        color: #34D399 !important;
        font-weight: bold;
    }
    .eco-terminal .term-step2 {
        color: #60A5FA !important;
        font-weight: bold;
    }
    .eco-terminal .term-step3 {
        color: #FBBF24 !important;
        font-weight: bold;
    }
    .eco-terminal .term-step4 {
        color: #F472B6 !important;
        font-weight: bold;
    }
    .eco-terminal .term-step5 {
        color: #A78BFA !important;
        font-weight: bold;
    }
    .eco-terminal .term-text {
        color: #E2E8F0 !important;
    }
    
    /* Elegant Custom Card Containers */
    .premium-card {
        background-color: #FFFFFF;
        padding: 1.5rem;
        border-radius: 18px;
        border: 1px solid #E2E8F0;
        box-shadow: 0 4px 6px -1px rgba(0,0,0,0.02), 0 2px 4px -1px rgba(0,0,0,0.01);
        margin-bottom: 1.25rem;
    }
    .accent-header-card {
        background-color: #2E7D32;
        color: #FFFFFF !important;
        padding: 2rem;
        border-radius: 24px;
        margin-bottom: 1.5rem;
        box-shadow: 0 10px 15px -3px rgba(46, 125, 50, 0.2);
        position: relative;
        overflow: hidden;
    }
    
    /* Interactive Elements Styling */
    .stButton>button {
        background-color: #2E7D32 !important;
        color: white !important;
        border-radius: 12px !important;
        padding: 0.5rem 1.5rem !important;
        font-weight: 600 !important;
        border: none !important;
        transition: all 0.3sease-in-out !important;
    }
    .stButton>button:hover {
        background-color: #1B5E20 !important;
        box-shadow: 0 4px 12px rgba(46,125,50,0.2) !important;
        transform: translateY(-1px);
    }
    
    /* Warning, Success, Alerts Badges */
    .badge-yellow {
        background-color: #FFD54F;
        color: #795548;
        padding: 0.35rem 0.85rem;
        border-radius: 8px;
        font-weight: bold;
        font-size: 0.8rem;
        display: inline-block;
    }
    .badge-blue {
        background-color: #E0F2FE;
        color: #0369A1;
        padding: 0.35rem 0.85rem;
        border-radius: 8px;
        font-weight: bold;
        font-size: 0.8rem;
        display: inline-block;
    }
    
</style>
""", unsafe_allow_html=True)

# ==============================================================================
# STATE & DATA INITIALIZATION
# ==============================================================================
if "auth_logged_in" not in st.session_state:
    st.session_state.auth_logged_in = False  # Default to premium auth gate
if "auth_user_name" not in st.session_state:
    st.session_state.auth_user_name = "Arjun Patel"
if "green_points" not in st.session_state:
    st.session_state.green_points = 1250
if "user_saved_plants" not in st.session_state:
    st.session_state.user_saved_plants = ["Holy Tulsi", "Cherry Tomato", "Spotted Aloe"]
if "chat_history" not in st.session_state:
    st.session_state.chat_history = [
        {"role": "assistant", "content": "Namaste! 🌿 I am PrakritiMitra, your nature-loving AI companion. I can speak multiple regional languages. Ask me anything about plant species, organic farming, or environmental science!"}
    ]
if "community_posts" not in st.session_state:
    st.session_state.community_posts = [
        {"author": "Radhika Rao", "time": "2 hours ago", "text": "Successfully established my drip-irrigation layout for organic gourds. Saving almost 4 liters daily!", "category": "💧 Irrigation", "likes": 14},
        {"author": "Anil Verma", "time": "Yesterday", "text": "Planted 5 neem saplings around our municipal library park! Green community drive going strong.", "category": "🌱 Tree Planting", "likes": 29},
        {"author": "Preeti Sen", "time": "3 days ago", "text": "Discovered mild early leaf spot on tomato. Clean pruned the bottom branches and misted cold-pressed neem sap solutions. Looking stable now!", "category": "🔍 Diagnostics", "likes": 18}
    ]
if "completed_challenges" not in st.session_state:
    st.session_state.completed_challenges = set(["Weekly Watering Challenge"])

# Dynamic Tips Database
ECO_TIPS = [
    "💧 Switch to localized drip watering pipelines which apply water drop-by-drop directly to bottom root-zones.",
    "🍁 Keep fallen dry leaves on the soil surface of plant beds as natural organic mulch. It preserves microbial vitality, blocks evaporation, and slowly releases nitrogen.",
    "☀️ Companion planting secret: Plant basil right next to tomato shrubs. It naturally repels whiteflies and mosquitoes while elevating plant yields!",
    "♻️ Boil banana peels in plain water for 24 hours to formulate an organic, potassium-dense liquid shot perfect for hibiscus, roses, and flowering shrubs."
]

# ==============================================================================
# PREMIUM AUTHENTICATION GATE SCREEN
# ==============================================================================
# ==============================================================================
# SIDEBAR NAVIGATION & AMBIENT WIDGETS
# ==============================================================================
tabs_options = [
    "🏠 Home Hub", 
    "🎯 Smart Recommendations", 
    "🔍 Leaf & Soil Diagnostic", 
    "🤖 PrakritiMitra Chat", 
    "📈 Water & Growth", 
    "📚 Learning Hub",
    "🌍 Community Grid",
    "🔄 Flow & Architecture",
    "👤 Profile & Auth"
]

if "current_tab" not in st.session_state:
    st.session_state.current_tab = "🏠 Home Hub"

with st.sidebar:
    st.markdown("<h1 style='color:#2E7D32; margin-top:0;'>🌱 EcoFriend</h1>", unsafe_allow_html=True)
    st.markdown("<p style='font-style:italic; font-size: 0.85rem; margin-top:-10px; color:#64748B;'>Growing a Greener Future with AI</p>", unsafe_allow_html=True)
    
    # Authenticated User Overview Card
    if st.session_state.auth_logged_in:
        st.markdown(f"""
        <div style="background-color: #E8F5E9; padding: 1rem; border-radius: 14px; border: 1px solid #A5D6A7; margin-bottom:1rem;">
            <p style="margin:0; font-size:0.75rem; color:#2E7D32; font-weight:bold; letter-spacing:0.5px;">ACTIVE GUARDIAN</p>
            <h4 style="margin: 0.2rem 0; color:#1B5E20 !important;">👤 {st.session_state.auth_user_name}</h4>
            <p style="margin: 0; font-size:0.85rem; color:#795548;">🏆 Level 4: <b>Tree Guardian</b></p>
        </div>
        """, unsafe_allow_html=True)
    else:
        st.info("🔓 Browse in Simulation mode. Sign in inside Profile tab to sync Green Points.")

    # Page Navigator
    st.markdown("---")
    st.markdown("<h3 style='margin-bottom:0.5rem; font-size:1.15rem; color:#2E7D32;'>📱 Page Selector</h3>", unsafe_allow_html=True)
    
    sidebar_select = st.radio(
        "Active Section",
        options=tabs_options,
        index=tabs_options.index(st.session_state.current_tab),
        label_visibility="collapsed"
    )
    if sidebar_select != st.session_state.current_tab:
        st.session_state.current_tab = sidebar_select
        st.rerun()

    st.markdown("---")

    # Real-Time Weather Intelligence Panel
    st.markdown("<h3 style='margin-bottom:0.5rem; font-size:1.15rem;'>🌦 Weather Intelligence</h3>", unsafe_allow_html=True)
    col_w1, col_w2 = st.columns(2)
    with col_w1:
        st.metric(label="Ambient Temp", value="29.4°C")
    with col_w2:
        st.metric(label="Soil Temp Sensor", value="26.1°C")
        
    st.markdown("""
        <div style="background-color:#E0F2FE; border-left:4px solid #0284C7; padding:0.6rem 0.8rem; border-radius:0 8px 8px 0; font-size:0.8rem; color:#0369A1; margin-bottom:1rem;">
            <b>Plantation Alert:</b> Warm climate index detected. Great solar irradiance window to introduce young <i>Tulsi</i> or <i>Tomato</i> sprouts outdoors.
        </div>
    """, unsafe_allow_html=True)
    
    # Gamification Progress Bar
    st.markdown("<h3 style='margin-bottom:0.1rem; font-size:1.15rem;'>🏆 Green Points (GP)</h3>", unsafe_allow_html=True)
    st.markdown(f"**{st.session_state.green_points} GP** earned towards next rank.")
    target_pts = 1500
    progress_val = st.session_state.green_points / target_pts
    st.progress(progress_val)
    st.caption(f"{target_pts - st.session_state.green_points} GP needed to raise to level 5: **Forest Master**")
    
    st.markdown("---")
    st.caption("🤖 EcoFriend Engine v2.0 • Powered by Gemini Vision & AI Core")

# ==============================================================================
# PREMIUM AUTHENTICATION GATE SCREEN ("Adv Auth Page")
# ==============================================================================
if not st.session_state.auth_logged_in:
    st.markdown("""
    <div style="text-align: center; margin-top: 1rem; margin-bottom: 1.5rem;">
        <span style="font-size: 3.5rem;">🛡️</span>
        <h1 style="margin-top: 0.5rem; color: #1B5E20;">Advanced Authentication Portal</h1>
        <p style="color: #64748B; font-size: 1.1rem; max-width: 650px; margin: 0 auto; line-height: 1.5;">
            Unlock custom telemetry sandboxes, compute smart crop instructions, scan leaf spots, and record historic plant catalogs.
        </p>
    </div>
    """, unsafe_allow_html=True)
    
    col_auth_left, col_auth_mid, col_auth_right = st.columns([1, 2.5, 1])
    
    with col_auth_mid:
        st.markdown("<div class='premium-card' style='background-color:#FFFFFF; border:2px solid #2E7D32; box-shadow: 0 10px 25px rgba(0,0,0,0.05);'>", unsafe_allow_html=True)
        st.markdown("<h2 style='font-size:1.35rem; color:#1B5E20; text-align:center; margin-bottom:1rem;'>🔑 Select Credentials Handshake Mode</h2>", unsafe_allow_html=True)
        auth_choice = st.selectbox("Select Access Portal Method", [
            "🔑 Email Sign-In", 
            "✨ Instant Account Registration", 
            "📲 SMS OTP Code Access", 
            "🛠️ Password Self-Recovery"
        ], label_visibility="collapsed")
        
        st.markdown("<hr style='margin-top:0.75rem; margin-bottom:1rem; border:0; border-top:1px solid #E2E8F0;'>", unsafe_allow_html=True)
        
        if auth_choice == "🔑 Email Sign-In":
            st.markdown("<h3 style='font-size:1.15rem; font-weight:700; color:#1E293B; margin-bottom:0.75rem;'>Secure Email Access</h3>", unsafe_allow_html=True)
            log_email = st.text_input("Registered Email Address", "arjun.patel@ecofriend.org", key="gate_log_email")
            log_pass = st.text_input("Security Password", "••••••••", type="password", key="gate_log_pass")
            
            col_lg1, col_lg2 = st.columns([1.5, 1])
            with col_lg1:
                if st.button("Unlock Sandbox Panel 🚪", use_container_width=True):
                    with st.spinner("Authorizing cryptographic token handshake..."):
                        time.sleep(1.0)
                    st.session_state.auth_logged_in = True
                    st.session_state.auth_user_name = log_email.split("@")[0].capitalize()
                    st.toast(f"Welcome back, {st.session_state.auth_user_name}! 🌱", icon="🌿")
                    st.rerun()
            with col_lg2:
                if st.button("🔓 Guest Mirror", use_container_width=True):
                    st.session_state.auth_logged_in = True
                    st.session_state.auth_user_name = "Guest Guardian"
                    st.toast("Entered simulation as Guest", icon="🔓")
                    st.rerun()
            
            st.markdown("<div style='text-align:center; color:#94A3B8; margin:1rem 0; font-size:0.75rem; letter-spacing:0.05em; font-weight:bold;'>OR SOCIAL SIGN-ON</div>", unsafe_allow_html=True)
            if st.button("🟢  Instant Log In with Google SSO", use_container_width=True):
                with st.spinner("Connecting to Google Identity API..."):
                    time.sleep(1.1)
                st.session_state.auth_logged_in = True
                st.session_state.auth_user_name = "Arjun Patel"
                st.toast("Google SSO Sign-In complete!", icon="✔️")
                st.rerun()
                
        elif auth_choice == "✨ Instant Account Registration":
            st.markdown("<h3 style='font-size:1.15rem; font-weight:700; color:#1E293B; margin-bottom:0.75rem;'>Create New Eco Profile</h3>", unsafe_allow_html=True)
            reg_user = st.text_input("Display Name / Persona", "Arjun Patel", key="gate_reg_user")
            reg_email = st.text_input("Your Active Email Address", "arjun.patel@ecofriend.org", key="gate_reg_email")
            reg_pass = st.text_input("Security Key / Password", "••••••••", type="password", key="gate_reg_pass")
            reg_coords = st.selectbox("Preferred Micro-climate Zone Coordinates", ["Hyderabad Hub (IN)", "Delhi Northern Plains (IN)", "Mumbai Coastal Zone (IN)", "Bengaluru Plateau (IN)"])
            
            if st.button("Register & Get +500 Welcome GP 🏆", use_container_width=True):
                with st.spinner("Registering securely on centralized database..."):
                    time.sleep(1.2)
                st.session_state.auth_logged_in = True
                st.session_state.auth_user_name = reg_user
                st.session_state.green_points = 500  # Starting validation bonus!
                st.toast(f"Account Ready, {reg_user}! +500 GP added.", icon="🎉")
                st.rerun()
                
        elif auth_choice == "📲 SMS OTP Code Access":
            st.markdown("<h3 style='font-size:1.15rem; font-weight:700; color:#1E293B; margin-bottom:0.75rem;'>SMS OTP Access</h3>", unsafe_allow_html=True)
            reg_phone = st.text_input("Phone Number Country Prefix", "+91 98765 43210", key="gate_reg_phone")
            
            col_otp1, col_otp2 = st.columns([1.6, 1])
            with col_otp1:
                input_otp = st.text_input("6-digit cellular OTP", "123456", key="gate_input_otp")
            with col_otp2:
                st.write("")
                st.write("")
                if st.button("Request Code"):
                    st.toast("SMS Verification Code Sent to Phone simulator!", icon="📲")
            
            if st.button("Verify Credentials 📲", use_container_width=True):
                with st.spinner("Verifying matching OTP code..."):
                    time.sleep(0.8)
                st.session_state.auth_logged_in = True
                st.session_state.auth_user_name = "SMS Guardian"
                st.toast("Verification Code Matched! Logged in.", icon="✔️")
                st.rerun()
                
        elif auth_choice == "🛠️ Password Self-Recovery":
            st.markdown("<h3 style='font-size:1.15rem; font-weight:700; color:#1E293B; margin-bottom:0.75rem;'>Dispatch Recovery Email</h3>", unsafe_allow_html=True)
            st.write("Forgot your keys? Enter your registered email address beneath to fetch an automated credentials recovery magic link.")
            recover_email = st.text_input("Profile Email Address", "arjun.patel@ecofriend.org", key="gate_recover_email")
            
            if st.button("Send Automated Recovery Email ✉️", use_container_width=True):
                with st.spinner("Dispatching secure validation token..."):
                    time.sleep(1.0)
                st.success(f"✉️ Password reset token has been successfully generated and sent to: {recover_email}")
                
        st.markdown("</div>", unsafe_allow_html=True)
        st.markdown("<p style='text-align:center; font-size:0.8rem; color:#64748B;'>🔒 Protected by TLS EcoFriend Cryptographic Sandbox Protocols</p>", unsafe_allow_html=True)
        
    # --- Always show lower navigation dock even when locked on authentication card ---
    st.markdown("<p style='margin-top: 4rem;'></p>", unsafe_allow_html=True)
    st.markdown("---")
    st.markdown("""
    <div style="background-color: #EBF8FF; padding: 1rem; border-radius: 14px; border: 1px solid #BEE3F8; margin-bottom:1.5rem; text-align: center;">
        <h3 style="margin: 0; color: #2B6CB0 !important; font-size: 1.15rem;">📱 EcoFriend Bottom Navigation Dock</h3>
        <p style="margin: 0; font-size: 0.8rem; color: #4A5568;">Click a tab below to jump directly to any active screen</p>
    </div>
    """, unsafe_allow_html=True)
    
    nav_cols_locked = st.columns(9)
    nav_icons_locked = {
        "🏠 Home Hub": "🏠 Home",
        "🎯 Smart Recommendations": "🎯 Recs",
        "🔍 Leaf & Soil Diagnostic": "🔍 Scan",
        "🤖 PrakritiMitra Chat": "🤖 Chat",
        "📈 Water & Growth": "📈 Growth",
        "📚 Learning Hub": "📚 Learn",
        "🌍 Community Grid": "🌍 Social",
        "🔄 Flow & Architecture": "🔄 Flow",
        "👤 Profile & Auth": "👤 Profile"
    }
    
    for idx, tab_name in enumerate(tabs_options):
        with nav_cols_locked[idx]:
            is_active = (st.session_state.current_tab == tab_name)
            short_name = nav_icons_locked[tab_name]
            
            if is_active:
                st.markdown(f"""
                <div style="text-align: center; border-bottom: 3px solid #2E7D32; padding-bottom: 2px;">
                    <p style="margin: 0; font-size: 0.8rem; font-weight: bold; color: #2E7D32;">{short_name.split()[0]}</p>
                </div>
                """, unsafe_allow_html=True)
                st.button(short_name, key=f"bottom_active_btn_locked_{idx}", use_container_width=True, disabled=True)
            else:
                if st.button(short_name, key=f"bottom_nav_btn_locked_{idx}", use_container_width=True):
                    st.session_state.current_tab = tab_name
                    st.rerun()
                    
    st.stop()  # Stop sequence if the user is not authenticated yet to display auth gate!

# ==============================================================================
# MAIN PAGE HEADER & BRAND SENTIMENT
# ==============================================================================
col_title, col_aux = st.columns([3, 1])
with col_title:
    st.title("EcoFriend AI Dashboard")
    st.markdown("<p style='color:#64748B; font-size:1rem; margin-top:-0.5rem;'>Unified Smart Agriculture & Smart Companion Sandbox Web Portal</p>", unsafe_allow_html=True)
with col_aux:
    # Quick active tip
    st.markdown(f"""
    <div style='background-color:#FFFBEB; border: 1px dashed #F59E0B; padding:0.5rem; border-radius:10px; font-size:0.75rem; color:#78350F;'>
        <b>🌱 Daily Eco-Tip:</b> {ECO_TIPS[0]}
    </div>
    """, unsafe_allow_html=True)

# State-driven interactive tab routing
tabs_options = [
    "🏠 Home Hub", 
    "🎯 Smart Recommendations", 
    "🔍 Leaf & Soil Diagnostic", 
    "🤖 PrakritiMitra Chat", 
    "📈 Water & Growth", 
    "📚 Learning Hub",
    "🌍 Community Grid",
    "🔄 Flow & Architecture",
    "👤 Profile & Auth"
]

if "current_tab" not in st.session_state:
    st.session_state.current_tab = "🏠 Home Hub"

active_tab = st.session_state.current_tab

# ==========================================
# TAB 1: HOME HUB
# ==========================================
if active_tab == "🏠 Home Hub":
    st.markdown("""
    <div class="accent-header-card">
        <span class="badge">SEASONAL TOP SELECTION</span>
        <div class="hero-title">🌱 Planting Season: Tulsi (Holy Basil)</div>
        <div style="font-size:1.1rem; opacity:0.9; max-width:80%; margin-bottom:1.5rem;">
            Ideal microclimate, dry loamy moisture level, and perfect ambient warmth index analyzed in your coordinates.
            Excellent companion for biological air filtration, herbal wellness, and backyard pot setups!
        </div>
        <div class="badge-yellow">🎯 Climate Match Score: 98% (Exceptional Match)</div>
    </div>
    """, unsafe_allow_html=True)
    
    # 3-Column Core Features Overview
    col_f1, col_f2, col_f3 = st.columns(3)
    
    with col_f1:
        st.markdown(f"""
        <div class="premium-card" style="height:100%;">
            <span style="font-size:2.5rem;">📈</span>
            <h4 style="margin:0.5rem 0 0.2rem 0;">Smart Growth Predictions</h4>
            <p style="color:#64748B; font-size:0.85rem; margin-bottom:1rem;">Simulate plant development based on soil properties, moisture records, and solar history.</p>
            <p style="font-size:0.85rem; color:#1B5E20; font-weight:bold;">👉 Navigate to 'Water & Growth' to simulate crops.</p>
        </div>
        """, unsafe_allow_html=True)
        
    with col_f2:
        st.markdown(f"""
        <div class="premium-card" style="height:100%;">
            <span style="font-size:2.5rem;">🔍</span>
            <h4 style="margin:0.5rem 0 0.2rem 0;">AI Leaf Pathology Diagnostic</h4>
            <p style="color:#64748B; font-size:0.85rem; margin-bottom:1rem;">Upload photos of spotted, withered, or yellow leaves to parse infections using computer vision.</p>
            <p style="font-size:0.85rem; color:#1B5E20; font-weight:bold;">👉 Go to 'Leaf & Soil Diagnostic' to check plant health.</p>
        </div>
        """, unsafe_allow_html=True)
        
    with col_f3:
        st.markdown(f"""
        <div class="premium-card" style="height:100%;">
            <span style="font-size:2.5rem;">🤖</span>
            <h4 style="margin:0.5rem 0 0.2rem 0;">PrakritiMitra Assistant</h4>
            <p style="color:#64748B; font-size:0.85rem; margin-bottom:1rem;">Your general AI companion with deep botanical intellect. Connects via Hindi, English, Telugu, and more!</p>
            <p style="font-size:0.85rem; color:#1B5E20; font-weight:bold;">👉 Go to 'PrakritiMitra Chat' to converse or hear speech output.</p>
        </div>
        """, unsafe_allow_html=True)

    # Active Challenges & Events
    st.markdown("### 🏆 Live Gamification Challenges")
    
    col_c1, col_c2, col_c3 = st.columns(3)
    challenges_list = [
        {"title": "🌳 Plant 5 Trees of Hope", "points": "+150 GP", "desc": "Sponsor/plant five native saplings around public or terrace spots.", "key": "Plant 5 Trees Challenge"},
        {"title": "💧 Weekly Micro Drip Watering", "points": "+80 GP", "desc": "Keep a stable water conservation drip timeline for 7 consecutive days.", "key": "Weekly Watering Challenge"},
        {"title": "🧪 Active Backyard Soil Audit", "points": "+100 GP", "desc": "Document manual pH checks or upload a macro soil photo.", "key": "Soil Audit Challenge"}
    ]
    
    for i, chal in enumerate([col_c1, col_c2, col_c3]):
        with chal:
            info = challenges_list[i]
            is_done = info["key"] in st.session_state.completed_challenges
            status_text = "✔️ Completed" if is_done else "⌛ In Progress"
            status_color = "#16A34A" if is_done else "#F59E0B"
            
            st.markdown(f"""
            <div style="background-color: #FFFFFF; padding: 1.2rem; border-radius: 14px; border: 1px solid #E2E8F0; position:relative;">
                <span style="float:right; background-color:#FEF3C7; color:#B45309; padding:0.15rem 0.6rem; border-radius:6px; font-size:0.75rem; font-weight:bold;">{info["points"]}</span>
                <h4 style="margin:0; font-size:1rem; color:#1E293B;">{info['title']}</h4>
                <p style="color:#64748B; font-size:0.8rem; margin:0.4rem 0 0.8rem 0;">{info['desc']}</p>
                <span style="font-size:0.8rem; color:{status_color}; font-weight:bold;">{status_text}</span>
            </div>
            """, unsafe_allow_html=True)
            if not is_done:
                if st.button(f"Mark as Completed", key=f"btn_chal_{i}"):
                    st.session_state.completed_challenges.add(info["key"])
                    st.session_state.green_points += 100
                    st.toast(f"🎉 Earned points! Green Points updated.", icon="🏆")
                    st.rerun()

# ==========================================
# TAB 2: SMART RECOMMENDATIONS
# ==========================================
if active_tab == "🎯 Smart Recommendations":
    st.subheader("🎯 Intelligent Agricultural & Nursery Recommender")
    st.write("Generate location-aware planting guidelines tailored around water volume constraints, ground space, and targets.")
    
    col_r1, col_r2 = st.columns([1, 1.2])
    with col_r1:
        st.markdown("<div class='premium-card'>", unsafe_allow_html=True)
        st.write("🔧 **Crop Matching Criteria**")
        loc_city = st.selectbox("Coordinates / Geolocation City", ["Hyderabad", "New Delhi", "Mumbai", "Bengaluru", "Chennai"])
        substrate_type = st.selectbox("Your Substrate Composition", ["Sandy Loam (Highly Porous)", "Clay Substrate (High Moisture Retaining)", "Peat Mix", "Loamy Forest Substrate"])
        space_avail = st.radio("Available Cultivation Layout", ["Terrace Pots / Small Balconies", "Backyard Raised Beds", "Full Agricultural Acreage"])
        goal_type = st.multiselect("Cultivation Purpose", ["Medicinal / Organic Oils", "High Fruit/Veggie Yield", "Ornamental Aesthetics", "Cooling / Shading Foliage"], default=["Medicinal / Organic Oils", "High Fruit/Veggie Yield"])
        
        recom_depth = st.slider("Target Maintenance Level (Low Care to High Cultivation)", 1, 5, 3)
        st.markdown("</div>", unsafe_allow_html=True)
        
        btn_recommend = st.button("Generate Plant Recommendations")
        
    with col_r2:
        if btn_recommend or "recom_ready" not in st.session_state:
            st.session_state.recom_ready = True
            with st.spinner("Decoding weather models & regional companion matrices..."):
                time.sleep(1.0)
                
            st.markdown("<div class='premium-card' style='background-color:#F8FAFC;'>", unsafe_allow_html=True)
            st.markdown("### 🌱 Suggested Eco-Matches")
            
            # Match 1
            st.markdown("""
            🤖 **1. Holy Tulsi (Ocimum tenuiflorum)**
            - **Match Confidence**: `96%`
            - **Target Substrate**: Loamy soil with 15% compost.
            - **Optimal Plantation Calendar**: Best planted during mid-May through August.
            - **Water requirement index**: Medium-Low volume (water early morning every alternative day).
            - **Native Companion Benefits**: Repels thrips and small gnats naturally. Excellent air-purifier.
            """, unsafe_allow_html=True)
            if st.button("Save Holy Tulsi to Profile Logs"):
                if "Holy Tulsi" not in st.session_state.user_saved_plants:
                    st.session_state.user_saved_plants.append("Holy Tulsi")
                    st.toast("Saved Holy Tulsi to your plant catalog!", icon="💾")
                else:
                    st.warning("Holy Tulsi is already in your catalog.")
            
            st.markdown("---")
            
            # Match 2
            st.markdown("""
            🍅 **2. Hybrid Vining Cherry Tomato**
            - **Match Confidence**: `89%`
            - **Target Substrate**: Loamy sand or porous potting soil rich in phosphorus.
            - **Optimal Plantation Calendar**: Spring to transition of monsoon peaks.
            - **Water requirement index**: High moist consistency (avoid dry spells during fruit sets).
            - **Native Companion Benefits**: Enriches root spaces when planted along French marigold flora.
            """, unsafe_allow_html=True)
            if st.button("Save Cherry Tomato to Profile Logs"):
                if "Cherry Tomato" not in st.session_state.user_saved_plants:
                    st.session_state.user_saved_plants.append("Cherry Tomato")
                    st.toast("Saved Cherry Tomato to your plant catalog!", icon="💾")
                else:
                    st.warning("Cherry Tomato is already in your catalog.")
                    
            st.markdown("</div>", unsafe_allow_html=True)

    # Climate Suitability Checker Detailed Submodule
    st.markdown("### 📊 Micro-Climate Suitability Index Evaluator")
    st.write("Input specialized soil and air metrics to retrieve an instant suitability score index.")
    col_cs1, col_cs2, col_cs3 = st.columns(3)
    with col_cs1:
        eval_plant = st.text_input("Species Variety Name", value="Holy Basil")
        eval_min_temp = st.number_input("Average Cold Temp (°C)", min_value=5, max_value=45, value=22)
    with col_cs2:
        eval_max_temp = st.number_input("Average Peak Hot Temp (°C)", min_value=15, max_value=60, value=35)
        eval_rainfall = st.slider("Target Irrigation Frequency (Weekly)", 1, 14, 5)
    with col_cs3:
        eval_humidity = st.selectbox("Relative Air Humidity Range", ["Dry (Arid, <30%)", "Temperate (35% - 60%)", "Monsoon Humid (>70%)"])
        
    if st.button("Calculate Specific Suitability Index", key="specific_suit_btn"):
        with st.spinner("Processing thermodynamic metrics..."):
            time.sleep(0.8)
            score = 100 - abs(eval_min_temp - 20) * 2 - abs(eval_max_temp - 32) * 1.5
            score = max(min(round(score, 1), 100.0), 10.0)
            
            if score >= 80.0:
                st.success(f"🟢 **Suitability Index: {score}% (Highly Compatible)**")
                st.write(f"The parameters fall well within the optimal physiological window of {eval_plant}. Minimal greenhouse cover is needed.")
            else:
                st.warning(f"🟡 **Suitability Index: {score}% (Suboptimal / Marginal)**")
                st.write(f"Provide afternoon partial sun net filters and drip systems to prevent moisture evaporative shock on leaf structures.")

# ==========================================
# TAB 3: LEAF & SOIL DIAGNOSTIC
# ==========================================
if active_tab == "🔍 Leaf & Soil Diagnostic":
    st.subheader("🔍 AI Leaf Disease Pathogen & Substrate Soil Decoder")
    st.write("Assess leaf infections or decode chemical substrate textures. Multi-tiered computer vision modeling.")
    
    col_d1, col_d2 = st.columns([1.1, 1])
    
    with col_d1:
        st.markdown("<div class='premium-card'>", unsafe_allow_html=True)
        st.markdown("#### 🍃 Leaf Pathology Diagnostic")
        st.write("Upload a direct top-down snapshot of an infected leaf to evaluate pathogens, bugs, or severe necrotic spots.")
        
        leaf_file = st.file_uploader("Upload Leaf Close-up Image", type=["jpg", "png", "jpeg"], key="master_leaf_upload")
        st.markdown("<p style='text-align:center; color:#94A3B8;'>— OR —</p>", unsafe_allow_html=True)
        demo_pick = st.selectbox("Inject Diagnostic Case Study Presets", [
            "Manual Upload Mode",
            "CASE 1: Wheat Stripe Rust (Puccinia striiformis)",
            "CASE 2: Tomato Early Blight (Alternaria solani)",
            "CASE 3: Dry Abiotic Citrus Solar Scorching",
            "CASE 4: Tea Red Creeping Rust (Cephaleuros virescens)"
        ])
        
        run_leaf = st.button("Run Leaf Vision Analysis")
        st.markdown("</div>", unsafe_allow_html=True)
        
        st.markdown("<div class='premium-card'>", unsafe_allow_html=True)
        st.markdown("#### 🪨 Substrate Soil Decipher")
        st.write("Define chemical, color and physical attributes to generate organic potassium-phosphate enrichment recipes.")
        soil_texture = st.select_slider("Texture Grains", ["Powdery Sand", "Loose Porous Loam", "Silt-Rich", "Dense Red Clay"])
        soil_ph = st.slider("Estimated pH Level (Buffer Index)", 4.0, 9.5, 6.5, step=0.1)
        organic_estimate = st.selectbox("Existing Raw Organic Humus Coverage", ["Scarce / Dry Ground", "Moderate / Standard Garden Soil", "Rich Fertile Mud"])
        
        run_soil = st.button("Decode Soil Substrate Profile")
        st.markdown("</div>", unsafe_allow_html=True)
        
    with col_d2:
        if run_leaf or (demo_pick != "Manual Upload Mode" and "leaf_scanned" not in st.session_state):
            st.session_state.leaf_scanned = True
            with st.spinner("Processing pathobiology visual vectors via CNN networks..."):
                time.sleep(1.2)
                
            st.markdown("<div class='premium-card' style='border: 1px solid #F87171; background-color: #FEF2F2;'>", unsafe_allow_html=True)
            st.markdown("### 🧫 AI Leaf Diagnostic Report")
            
            if "CASE 1" in demo_pick or (leaf_file and "rust" in leaf_file.name.lower()):
                st.error("🚨 **Infection Detected: Stripe Rust (Puccinia striiformis)**")
                st.markdown("""
                - **Severity Index**: **HIGH** (Covers ~48% of active leaf canopy)
                - **Primary Etiology**: High damp condensation at nighttime combined with low wind flow.
                - **Organic Remediation Plan**:
                  1. Actively prune infested bottom branches; package and incinerate them off-site.
                  2. Mist localized cold-pressed neem oils combined with 0.5% soda water.
                  3. Restructure layout spaces to decrease canopy damp density.
                """)
            elif "CASE 2" in demo_pick or (leaf_file and "tomato" in leaf_file.name.lower()):
                st.warning("⚠️ **Infection Detected: Early Blight (Alternaria solani)**")
                st.markdown("""
                - **Severity Index**: **MODERATE** (Target-like concentric circle brown spots spotted)
                - **Primary Etiology**: Spores splashing up from raw top soil onto foliage during heavy surface watering.
                - **Organic Remediation Plan**:
                  1. Eliminate raw overhead sprinkler spraying; irrigate bottom roots via slow drippers.
                  2. Mulch the bottom root soil bed with organic coconut coir fibers.
                  3. Mist bio-fungicide containing *Bacillus subtilis* to slow spore production.
                """)
            elif "CASE 3" in demo_pick:
                st.success("🟢 **Abiotic Stress Detected: Solar Scorching / Wind Shock**")
                st.markdown("""
                - **Severity Index**: **LOW** (Outer leaf margin dryness, no active pathogenic spread)
                - **Primary Etiology**: Dry thermal heat accompanied by excessive direct ultraviolet index.
                - **Organic Remediation Plan**:
                  1. Erect a simple 30% partial-shade green nursery net.
                  2. Avoid water drops dry-browning on foliage during hot midday heat.
                """)
            else:
                st.error("🚨 **Infection Detected: Tea Red Creeping Rust (Cephaleuros virescens)**")
                st.markdown("""
                - **Severity Index**: **MODERATE** (Orange-brown felt-like velvet colonies)
                - **Primary Etiology**: Algal parasite multiplying on moisture-saturated hosts in humid, shaded environments.
                - **Organic Remediation Plan**:
                  1. Prune highly nested lower air corridors to accelerate daytime sun drying.
                  2. Utilize copper-based organic dusts early in the seasonal cycle.
                """)
            st.markdown("</div>", unsafe_allow_html=True)
            
        if run_soil:
            with st.spinner("Decoding substrate components..."):
                time.sleep(0.8)
            st.markdown("<div class='premium-card' style='border: 1px solid #34D399; background-color: #ECFDF5;'>", unsafe_allow_html=True)
            st.markdown("### 🪨 Soil Analysis & Nutrient Output")
            st.markdown(f"""
            - **Analyzed Substrate Class**: **{soil_texture}** with {organic_estimate} base
            - **Buffered pH Assessment**: **{soil_ph}** ({"Acidic" if soil_ph < 6 else "Alkaline" if soil_ph > 7.2 else "Optimal Neutral"})
            - **Recommended Organic Fertilizers**:
              1. Add aged kitchen compost and cow manure to bulk organic carbon.
              2. Add ground eggshells (calcium boost) or wood ashes to adjust base parameters if acidic.
              3. Dilute rock phosphate powder inside potting mixes to promote resilient root webs.
            """)
            st.markdown("</div>", unsafe_allow_html=True)

    # AI Weather-Disease Prediction model
    st.markdown("### 🌦 Weather-Driven Interactive Disease Prediction Risk")
    st.write("Configure environmental triggers to forecast upcoming fungal or bacterial disease risks prior to outbreaks.")
    
    p_spec = st.selectbox("Subject Plant Crop", ["Solanaceous (Tomato, Pepper, Eggplant)", "Cereal grain crops", "Succulent Herbs"])
    p_wet_hrs = st.slider("Continuous Foliar Wetness (Hours daily)", 0, 24, 6)
    p_heat = st.slider("Expected Humidity Index (%)", 10, 100, 65)
    
    if st.button("Generate Disease Outbreak Risk Map"):
        risk = (p_wet_hrs * 3.5) + (p_heat * 0.5)
        risk = min(max(round(risk, 0), 0), 100)
        
        if risk < 35:
            st.success(f"🟢 Outbreak Risk Level: {risk}% (Low). Foliar tissues are adequately dry.")
        elif risk < 70:
            st.warning(f"🟡 Outbreak Risk Level: {risk}% (Moderate). Monitor lower stems for micro-fungal rings.")
        else:
            st.error(f"🚨 Outbreak Risk Level: {risk}% (High Threat!). Fungal spores will hatch soon. Spray neem bio-remedies immediately!")

# ==========================================
# TAB 4: PRAKRITIMITRA COMPANION CHAT
# ==========================================
if active_tab == "🤖 PrakritiMitra Chat":
    st.markdown("""
    <div style="background-color:#E8F5E9; padding:1.25rem; border-radius:18px; border:1px solid #C8E6C9; margin-bottom:1rem;">
        <h3 style="color:#2E7D32; margin:0 0 0.25rem 0;">🤖 PrakritiMitra Chat Screen</h3>
        <p style="margin:0; font-size:0.95rem; color:#1B5E20;">A sophisticated botanical companion capable of advising on organic farming, crop rotation, regional conservation, or general daily learning topics.</p>
    </div>
    """, unsafe_allow_html=True)
    
    col_chat1, col_chat2 = st.columns([1, 2.8])
    with col_chat1:
        st.markdown("<div class='premium-card'>", unsafe_allow_html=True)
        st.markdown("#### ⚙️ Companion Modes")
        chat_mode = st.radio("Intelligence Personality Profile", [
            "🌿 Specialized Botanist (Focuses purely on botany, disease indexes, gardening workflows, soil dynamics)",
            "🧠 General AI Assistant (Capable of helping with broad study questions, study planning, everyday essays)"
        ])
        
        st.markdown("#### 🌍 Multilingual Intelligence")
        selected_lang = st.selectbox("Preferred Conversation Language", [
            "English", "Hindi (हिन्दी)", "Telugu (తెలుగు)", "Tamil (தமிழ்)", 
            "Kannada (ಕನ್ನಡ)", "Malayalam (മലയാളം)", "Bengali (বাংলা)", "Spanish (Español)"
        ])
        
        st.markdown("#### 🎙 Speech & Voice Controls")
        voice_tts = st.toggle("Enable TTS (Text-to-Speech) Audio Synthesizer Output", value=True)
        use_mic = st.toggle("Simulate Speech-to-Text Voice Microphone Input", value=False)
        st.markdown("</div>", unsafe_allow_html=True)
        
    with col_chat2:
        # Chat log display
        for chat in st.session_state.chat_history:
            role_icon = "🤖" if chat["role"] == "assistant" else "👤"
            with st.chat_message(chat["role"], avatar=role_icon):
                st.write(chat["content"])
                
        # Simulate local mic input preview
        if use_mic:
            st.info("🎙 **Mic Listening Simulator active...** Tap below to choose what you want to say by voice:")
            simulated_speech = st.radio("Choose Voice Input Sentence:", [
                "How do I naturally treatment fungal leaves?",
                "Suggest a companion plant layout for balconies.",
                "How long does organic compost take to decompose?",
                "What is the difference between monocot and dicot leaves?"
            ])
            if st.button("Synthesize Speech Input 🎤"):
                st.session_state.chat_history.append({"role": "user", "content": simulated_speech})
                st.rerun()

        user_input = st.chat_input("Ask PrakritiMitra anything about nature or general things...")
        
        if user_input:
            st.session_state.chat_history.append({"role": "user", "content": user_input})
            st.rerun()
            
        # Parse last user message and write response
        if len(st.session_state.chat_history) > 0 and st.session_state.chat_history[-1]["role"] == "user":
            user_prompt = st.session_state.chat_history[-1]["content"]
            
            with st.chat_message("assistant", avatar="🤖"):
                with st.spinner("Formulating biological suggestions..."):
                    time.sleep(1.0)
                
                low_p = user_prompt.lower()
                
                # Dynamic localized response matching selected language
                if "hindi" in selected_lang.lower():
                    base_resp = "नमस्ते! "
                elif "telugu" in selected_lang.lower():
                    base_resp = "నమస్కారం! "
                elif "tamil" in selected_lang.lower():
                    base_resp = "வணக்கம்! "
                else:
                    base_resp = "Greetings! "
                    
                if "yellow" in low_p or "fungal" in low_p or "leaves" in low_p:
                    ans = base_resp + "Yellow leaf tips (chlorosis) normally signal either root saturation or early stage nitrogen mineral flushouts. Check the soil moisture first. If it's soggy, halt watering for 3 days and spray low level organic compost tea."
                elif "companion" in low_p:
                    ans = base_resp + "A marvelous layout idea: Plant basil right next to tomato shrubs. It naturally blocks whiteflies. For vertical pots, plant deep carrot sprouts nested alongside garlic heads."
                elif "compost" in low_p:
                    ans = base_resp + "Standard organic composting under aerobic conditions takes roughly 4 to 12 weeks to completely disintegrate into black gold. Turn the composting piles every 10 days to support oxygen ingestion by friendly micro-bacteria!"
                elif "monocot" in low_p or "difference" in low_p:
                    ans = base_resp + "Fascinating science question! Monocots typically features leaves with parallel vein grids (like wheat or palms), whereas dicots display sophisticated web-like branching veins (like rose bushes or oaks)."
                else:
                    ans = base_resp + "Fascinating query! I suggest checking our microclimate suitability checklist to inspect matches, or review the Eco Encyclopedia for detailed biological data steps."
                
                st.session_state.chat_history.append({"role": "assistant", "content": ans})
                st.write(ans)
                
                # Audio simulator if TTS toggled
                if voice_tts:
                    st.write("🔊 *Synthetic Speech Audio Stream Output:*")
                    # Inject a clean simulated sound bar
                    audio_data = np.sin(np.linspace(0, 3000, 3000))
                    st.audio(audio_data, format="audio/wav", sample_rate=16000)

# ==========================================
# TAB 5: WATER & GROWTH
# ==========================================
if active_tab == "📈 Water & Growth":
    st.subheader("💧 Smart Irrigation & Growth Simulation Modeler")
    st.write("Calculate precision water needs or forecast crop growth height, yield, and blossom calendars with custom telemetry inputs.")
    
    col_w1, col_w2 = st.columns([1, 1.2])
    
    with col_w1:
        st.markdown("<div class='premium-card'>", unsafe_allow_html=True)
        st.markdown("#### 🚿 Precision Water Management Calculator")
        st.write("Calculates daily water volumes by matching localized evapotranspiration coefficients.")
        
        target_crop = st.selectbox("Plant Name Category", ["Holy Basil (Tulsi)", "Cherry Tomato", "Spotted Aloe Vera", "Carrot Roots", "French Marigold"])
        stage = st.selectbox("Present Growth Phase", ["Sprouting/Germination Seedling", "Rapid Vegetative Shrub", "Flowering Stage", "Fruit Maturity"])
        soil_moisture_level = st.slider("Soil Moisture Content (Telemetry Percent)", 10, 100, 45)
        evapo_index = st.slider("Day Ambient Heat Loss Coefficient (ET0)", 1.0, 12.0, 4.5, step=0.1)
        
        btn_calc_water = st.button("Calculate Water Irrigation Command")
        st.markdown("</div>", unsafe_allow_html=True)
        
        st.markdown("<div class='premium-card'>", unsafe_allow_html=True)
        st.markdown("#### 📊 Smart Growth Trend Predictor")
        st.write("Tune fertilizer and soil configurations to project growth rates.")
        fert_dose = st.select_slider("Aged Bio-Compost Dosage", ["None", "Low / Maintenance", "Standard Balanced", "Highly Fertilized Booster"])
        soil_aeration = st.slider("Soil Pot Aeration Rating (%)", 10, 100, 75)
        
        btn_sim_growth = st.button("Run Future Growth Simulation")
        st.markdown("</div>", unsafe_allow_html=True)
        
    with col_w2:
        if btn_calc_water or "water_calc_ready" not in st.session_state:
            st.session_state.water_calc_ready = True
            
            # Simple standard agricultural ET water requirements check
            crop_coef = {"Holy Basil (Tulsi)": 0.7, "Cherry Tomato": 1.1, "Spotted Aloe Vera": 0.35, "Carrot Roots": 0.8, "French Marigold": 0.6}
            kc = crop_coef.get(target_crop, 0.7)
            
            # Calculation: Water req = (Evapotranspiration * Crop Coefficient) - moisture bonus
            water_req = (evapo_index * kc) * (1 - (soil_moisture_level / 150))
            water_req_l = max(round(water_req * 0.25, 2), 0.05)
            
            st.markdown("<div class='premium-card' style='border: 1px solid #60A5FA; background-color: #EFF6FF;'>", unsafe_allow_html=True)
            st.markdown("### 💧 Irrigation Report")
            st.markdown(f"""
            - **Target crop species**: *{target_crop}* ({stage})
            - **Evaluated Water Requirement**: **{water_req_l} Liters** per container/pot daily.
            - **Irrigation Schedule Advice**: Watering interval optimal calculated early in morning hours to trim evaporation losses.
            - **Conservation Recommendation**: Install coconut mulch coir to reduce current water requirements by another `20%`!
            """)
            st.markdown("</div>", unsafe_allow_html=True)
            
        if btn_sim_growth:
            st.markdown("<div class='premium-card'>", unsafe_allow_html=True)
            st.markdown("### 📈 Projected Height Development Simulator")
            st.write("Visualizes plant height projections over a 12-week timeline.")
            
            # Generate simulation growth data
            weeks = [f"Wk {w}" for w in range(1, 13)]
            base_coeff = 2.5 if fert_dose == "None" else 4.0 if fert_dose == "Low / Maintenance" else 6.2 if fert_dose == "Standard Balanced" else 8.5
            heights = [round(base_coeff * (w**0.8) * (soil_aeration/100), 1) for w in range(1, 13)]
            
            chart_df = pd.DataFrame({"Weeks": weeks, "Expected Plant Height (cm)": heights})
            st.line_chart(chart_df.set_index("Weeks"))
            
            st.write(f"🌟 **Simulation Outcomes:** With *{fert_dose}* composting, your crop will reach approximately **{heights[-1]} cm** at week 12. Expected blossom buds will start surfacing around week 7–8.")
            st.markdown("</div>", unsafe_allow_html=True)

# ==========================================
# TAB 6: ECO LEARNING HUB
# ==========================================
if active_tab == "📚 Learning Hub":
    st.subheader("📚 Eco-Learning Hub & Botanical Encyclopedia")
    st.write("Browse detailed organic cultivation schedules and plant databases.")
    
    encyclopedia_search = st.text_input("🔍 Search Encyclopedia (e.g. Neem, Basil, Aloe, Mint)", "")
    
    # Grid of plant datacards
    col_p1, col_p2, col_p3 = st.columns(3)
    
    plants_db = [
        {"name": "🌱 Oicmum Tulsi (Holy Basil)", "type": "Medicinal & Holy", "desc": "Great for balcony locations. Tolerates sunny coordinates. Highly therapeutic, improves home air purification indexes.", "tip": "Prune apical buds regularly to stimulate side bushy stems."},
        {"name": "🍅 Solanum Tomato", "type": "Foliar Garden Crop", "desc": "Demands robust solar coverage. Benefit from supportive stakes. Loves ground mulch grids.", "tip": "Pinch early suckers to preserve central stem yield."},
        {"name": "🍀 Aloe Spicata", "type": "Succulent Shrub", "desc": "Requires rare water intervals. Avoid tight clay water pools. Tolerates high temperature indices.", "tip": "Use sandy gravelly substrate matrices."}
    ]
    
    for idx, pcol in enumerate([col_p1, col_p2, col_p3]):
        with pcol:
            p_data = plants_db[idx]
            st.markdown(f"""
            <div class="premium-card">
                <span style="background-color:#E8F5E9; color:#2E7D32; padding:0.25rem 0.6rem; border-radius:6px; font-size:0.75rem; font-weight:bold;">{p_data['type']}</span>
                <h4 style="margin:0.5rem 0 0.2rem 0;">{p_data['name']}</h4>
                <p style="color:#64748B; font-size:0.85rem;">{p_data['desc']}</p>
                <div style="background-color:#FFFBEB; padding:0.5rem; border-radius:8px; font-size:0.8rem; color:#D97706; margin-top:0.5rem;">
                    <b>💡 Cultivation Tip:</b> {p_data['tip']}
                </div>
            </div>
            """, unsafe_allow_html=True)

    st.markdown("### 🎬 Practical Organic Gardening Tutorials")
    col_v1, col_v2 = st.columns(2)
    with col_v1:
        st.markdown("""
        <div class="premium-card">
            <h4>🍂 How to Build the Ideal Compost Heap at Home</h4>
            <p style="color:#64748B; font-size:0.85rem;">A study detailing optimal green nitrogen vs brown carbon ratios (30:1) to produce highly active bio-rich organic fertilizers with minimal odor.</p>
            <p style="font-size:0.8rem; color:#2E7D32;">⏱️ <i>Estimated reading time: 6 mins</i></p>
        </div>
        """, unsafe_allow_html=True)
    with col_v2:
        st.markdown("""
        <div class="premium-card">
            <h4>🌿 High-Yield Pruning Schemes for Balcony Flora</h4>
            <p style="color:#64748B; font-size:0.85rem;">Learn leaf branching tricks and canopy shaping methodologies to boost herbal density by up to 2.5x, even under restricted direct sunlight.</p>
            <p style="font-size:0.8rem; color:#2E7D32;">⏱️ <i>Estimated reading time: 8 mins</i></p>
        </div>
        """, unsafe_allow_html=True)

# ==========================================
# TAB 7: COMMUNITY GRID
# ==========================================
if active_tab == "🌍 Community Grid":
    st.subheader("🌍 EcoFriend Active Community Board")
    st.write("Engage with organic gardeners around your city. Showcase green milestones and take active environmental pledges.")
    
    col_co1, col_co2 = st.columns([1, 1.8])
    with col_co1:
        st.markdown("<div class='premium-card'>", unsafe_allow_html=True)
        st.markdown("#### 📢 Post a Community Progress Update")
        post_text = st.text_area("Share your plant's status or share a green tip!", placeholder="My cherry tomato saplings are showing beautiful golden blossoms... 🌱🍅")
        post_category = st.selectbox("Category Tags", ["🌱 Tree Planting", "💧 Irrigation", "🔍 Diagnostics", "💡 General Advice"])
        
        if st.button("Publish to Community Grid"):
            if post_text:
                new_post = {
                    "author": st.session_state.auth_user_name,
                    "time": "Just now",
                    "text": post_text,
                    "category": post_category,
                    "likes": 0
                }
                st.session_state.community_posts.insert(0, new_post)
                st.session_state.green_points += 50
                st.toast("Post shared! Placed +50 Green Points into your dashboard.", icon="🎉")
                st.rerun()
            else:
                st.error("Please fill in some message content.")
        st.markdown("</div>", unsafe_allow_html=True)
        
        st.markdown("<div class='premium-card'>", unsafe_allow_html=True)
        st.markdown("#### 🌱 Active Environmental Pledges")
        st.markdown("""
        - 🥤 **Pledge 1**: Refined from buying single-use plastics this week (+20 GP)
        - 💡 **Pledge 2**: Repurpose organic water water outputs to saturate turf (+40 GP)
        """)
        if st.button("Take All Pledges"):
            st.session_state.green_points += 60
            st.success("🎉 Outstanding commitment! You have committed to beautiful environmental actions.")
            st.rerun()
        st.markdown("</div>", unsafe_allow_html=True)
        
    with col_co2:
        st.markdown("#### 💬 Latest Community Story board")
        for idx, post in enumerate(st.session_state.community_posts):
            st.markdown(f"""
            <div class="premium-card">
                <span style="float:right; background-color: #EEF2F6; color: #475569; padding: 0.2rem 0.6rem; border-radius: 6px; font-size: 0.75rem; font-weight: bold;">{post['category']}</span>
                <p style="margin:0; font-weight:bold; color: #1E293B;">👤 {post['author']} <span style="font-weight:normal; font-size:0.8rem; color:#94A3B8;">• {post['time']}</span></p>
                <p style="margin: 0.5rem 0; color:#334155; font-size:1rem;">"{post['text']}"</p>
            </div>
            """, unsafe_allow_html=True)
            if st.button(f"💖 Upvote Story ({post['likes']} likes)", key=f"btn_like_{idx}"):
                st.session_state.community_posts[idx]["likes"] += 1
                st.rerun()

# ==========================================
# TAB 7.5: WORKFLOW & DEEP ARCHITECTURE FLOW
# ==========================================
if active_tab == "🔄 Flow & Architecture":
    st.subheader("🔄 Interactive System Architecture & Application Workflows")
    st.write("Examine the visual pathways, backend pipelines, predictive AI models, and real-time state machines powering the EcoFriend engine.")
    
    # SYSTEM OVERVIEW ARCHITECTURE DIAGRAM (HTML/CSS representation)
    st.markdown("""
    <div style="background-color: #F8FAFC; border: 1px solid #E2E8F0; padding: 1.5rem; border-radius: 12px; margin-bottom: 2rem;">
        <h4 style="color: #0F172A; margin: 0 0 1rem 0; font-size: 1.15rem; display: flex; align-items: center; gap: 0.5rem;">
            <span>🧱</span> Overall System Architecture Flow
        </h4>
        <div style="display: flex; flex-direction: column; align-items: center; gap: 0.75rem; font-family: monospace; font-size: 0.9rem;">
            <div style="background-color: #E2E8F0; border-left: 5px solid #22C55E; padding: 0.5rem 1rem; border-radius: 6px; width: 80%; text-align: center; box-shadow: 0 2px 4px rgba(0,0,0,0.05);">
                <b style="color: #1E293B;">🖥️ Streamlit Frontend</b><br>
                <span style="font-size: 0.75rem; color: #64748B;">Receives User inputs, image uploads, chat queries, and slider triggers</span>
            </div>
            <div style="color: #22C55E; font-weight: bold; font-size: 1.2rem;">⬇️</div>
            <div style="background-color: #D1FAE5; border-left: 5px solid #10B981; padding: 0.5rem 1rem; border-radius: 6px; width: 80%; text-align: center; box-shadow: 0 2px 4px rgba(0,0,0,0.05);">
                <b style="color: #065F46;">⚙️ Python Backend Engine & API Gateways</b><br>
                <span style="font-size: 0.75rem; color: #047857;">State handlers, session tokens, precision matrix algorithms, weather routing</span>
            </div>
            <div style="color: #10B981; font-weight: bold; font-size: 1.2rem;">⬇️</div>
            <div style="background-color: #DBEAFE; border-left: 5px solid #3B82F6; padding: 0.5rem 1rem; border-radius: 6px; width: 80%; text-align: center; box-shadow: 0 2px 4px rgba(0,0,0,0.05);">
                <b style="color: #1E3A8A;">🧠 AI Models & Predictions (Gemini / CV CNN / LLM)</b><br>
                <span style="font-size: 0.75rem; color: #1D4ED8;">Feature extraction, leaf classification, intent mapping, growth timeline forecasts</span>
            </div>
            <div style="color: #3B82F6; font-weight: bold; font-size: 1.2rem;">⬇️</div>
            <div style="background-color: #FEF3C7; border-left: 5px solid #F59E0B; padding: 0.5rem 1rem; border-radius: 6px; width: 80%; text-align: center; box-shadow: 0 2px 4px rgba(0,0,0,0.05);">
                <b style="color: #78350F;">💾 Database & Persistence (SQLite / State Handlers)</b><br>
                <span style="font-size: 0.75rem; color: #D97706;">Saves regional plant lists, active community logs, gamified environmental points</span>
            </div>
            <div style="color: #F59E0B; font-weight: bold; font-size: 1.2rem;">⬇️</div>
            <div style="background-color: #F0FDF4; border-left: 5px solid #15803D; padding: 0.5rem 1rem; border-radius: 6px; width: 80%; text-align: center; box-shadow: 0 2px 4px rgba(0,0,0,0.05);">
                <b style="color: #166534;">📊 Output UI Presentation Layer</b><br>
                <span style="font-size: 0.75rem; color: #15803D;">Dynamic dashboard updates, live maps, confidence heatmaps, diagnostic results</span>
            </div>
        </div>
    </div>
    """, unsafe_allow_html=True)

    col_wflow1, col_wflow2 = st.columns([1.2, 1])
    
    with col_wflow1:
        st.markdown("<div class='premium-card'>", unsafe_allow_html=True)
        st.markdown("#### 🚀 Interactive Workflow Selector")
        st.write("Choose any of the core functional workflows below to highlight its journey steps and simulate its execution live!")
        
        flow_choice = st.selectbox("Select Application Flow to Inspect", [
            "🔐 Authentication Flow (Login/Signup)",
            "🏠 Home Dashboard Navigation Flow",
            "🌿 Plant Recommendation Process Flow",
            "📷 Leaf Scan (Disease Detection & Classification)",
            "🤖 PrakritiMitra Chatbot Query Path",
            "📈 Growth Prediction Model Journey",
            "🌦 Weather Integration & Live Sync Flow",
            "🌍 Community Grid Social & Storage System",
            "🏆 Gamification & Points Accruing Loop",
            "👤 Profile Maintenance & Logs Flow"
        ])
        
        st.markdown("<hr style='margin:1rem 0;'>", unsafe_allow_html=True)
        
        # Display Flow Details based on selection
        if flow_choice == "🔐 Authentication Flow (Login/Signup)":
            st.markdown("""
            ##### 🔐 Workflow Details: Authentication
            - **Step 1:** User accesses sign-in portal choosing *Email/Password login*, *Google One-Tap SSO*, or *OTP Cellular confirmation*.
            - **Step 2:** Submission prompts secure cryptographic handshake.
            - **Step 3:** System creates or maps a structured, active session state (`st.session_state`).
            - **Step 4:** Corresponding user metadata (username, custom region, accumulated EcoPoints) are loaded globally.
            """)
        elif flow_choice == "🏠 Home Dashboard Navigation Flow":
            st.markdown("""
            ##### 🏠 Workflow Details: Home Dashboard
            - **Step 1:** Successful authentication unlocks global dashboard views.
            - **Step 2:** Home Hub fetches local cache parameters (dynamic weather, default tulsi score).
            - **Step 3:** Displays unified widgets: *Weather updates*, *Climate recommendations*, *Eco Tips of the day*, and *Dynamic progress bars*.
            - **Step 4:** Exposes fast redirection shortcuts directing back to scanning or chat engines.
            """)
        elif flow_choice == "🌿 Plant Recommendation Process Flow":
            st.markdown("""
            ##### 🌿 Workflow Details: Plant Recommendation
            - **Step 1:** User inputs location attributes, optional soil classification, and plantation goal (shade, yield, or health).
            - **Step 2:** Python recommendation engines query the local horticultural database.
            - **Step 3:** Compares climate specifications (temperature tolerances, humidity margins) matching candidate varieties.
            - **Step 4:** Returns sorted, optimal recommendations backed by precise care parameters and specific watering metrics.
            """)
        elif flow_choice == "📷 Leaf Scan (Disease Detection & Classification)":
            st.markdown("""
            ##### 📷 Workflow Details: Leaf Scanner CNN
            - **Step 1:** User snaps or uploads a leaf picture.
            - **Step 2:** Byte-stream is passed down to classification nodes (neural network representation).
            - **Step 3:** Feature extraction calculates pathological signals matching known rusts, molds, or deficiencies.
            - **Step 4:** Outputs diagnostic names, confidence scores, hazard levels (Severity), and organic remedies.
            """)
        elif flow_choice == "🤖 PrakritiMitra Chatbot Query Path":
            st.markdown("""
            ##### 🤖 Workflow Details: PrakritiMitra LLM
            - **Step 1:** User submits a textual query or spoken question.
            - **Step 2:** Input is routed to semantic intent classifier hooks.
            - **Step 3:** High confidence agricultural/botany requests route through deep botanical systems; general chat routes to base friendly tone engines.
            - **Step 4:** Renders formatted solutions directly with conversational context streaming.
            """)
        elif flow_choice == "📈 Growth Prediction Model Journey":
            st.markdown("""
            ##### 📈 Workflow Details: Growth Predictor
            - **Step 1:** User selects target plant variety from local inventory.
            - **Step 2:** Fetches historical species development data combined with ambient weather trends.
            - **Step 3:** Simulates potential growth vectors over a flexible temporal index.
            - **Step 4:** Displays an interactive chart illustrating expected height trajectories, blossoming milestones, and yield estimates.
            """)
        elif flow_choice == "🌦 Weather Integration & Live Sync Flow":
            st.markdown("""
            ##### 🌦 Workflow Details: Weather Engine
            - **Step 1:** System periodically triggers background asynchronous weather requests.
            - **Step 2:** Fetches real-time temperature, absolute humidity, and local ambient precipitation possibilities.
            - **Step 3:** Automatically updates agricultural metrics and microclimate matchmaking states.
            - **Step 4:** Adapts dynamic alert banners when extreme conditions are sensed.
            """)
        elif flow_choice == "🌍 Community Grid Social & Storage System":
            st.markdown("""
            ##### 🌍 Workflow Details: Community Grid
            - **Step 1:** Active user joins neighborhood challenges or composes an organic gardening milestone update.
            - **Step 2:** Image streams or text narratives list inside synchronized application memory databases.
            - **Step 3:** Content immediately updates on the live community blackboard feed available to all adjacent profiles.
            - **Step 4:** Enables interaction events (such as upvotes and shares) updating author metadata.
            """)
        elif flow_choice == "🏆 Gamification & Points Accruing Loop":
            st.markdown("""
            ##### 🏆 Workflow Details: Gamification and Badges
            - **Step 1:** Monitors discrete sustainable actions (daily logs, diagnostics completed, organic pledges checked).
            - **Step 2:** Increments absolute profile EcoPoints (Green Points) with corresponding state toast notices.
            - **Step 3:** Compares current scores against dynamic experience margins.
            - **Step 4:** Automatically registers verified digital certificates, lock achievements, and ranks.
            """)
        elif flow_choice == "👤 Profile Maintenance & Logs Flow":
            st.markdown("""
            ##### 👤 Workflow Details: Profile Dashboard
            - **Step 1:** Loads centralized session values mapping onto the personal data container.
            - **Step 2:** Renders registered plant inventories, ongoing daily calendars, and analytics.
            - **Step 3:** Displays unlocked digital badges confirming sustainability actions.
            - **Step 4:** Enables local sandbox logout or authentication resetting commands.
            """)
            
        st.markdown("</div>", unsafe_allow_html=True)
        
    with col_wflow2:
        st.markdown("<div class='premium-card' style='background-color: #F8FAFC; border:1px solid #CBD5E1;'>", unsafe_allow_html=True)
        st.markdown("#### ⚡ Real-Time Pipeline Simulator")
        st.write("Run the automated mock sequence of the selected pipeline to watch input data translate into output displays in real time!")
        
        sim_speed = st.slider("Simulation Animation Step-Delay (Seconds)", 0.2, 2.0, 0.8)
        
        run_sim = st.button("▶️ Execute Selected Pipeline Workflow", use_container_width=True)
        
        st.write("")
        st.markdown("<div class='eco-terminal' style='background-color:#0F172A; padding:1.25rem; border-radius:10px; font-size:0.85rem; border:1px solid #334155; min-height: 250px;'>", unsafe_allow_html=True)
        st.markdown("<span class='term-comment'># -- eco-pipeline terminal initialized --</span>", unsafe_allow_html=True)
        st.markdown("<span class='term-waiting'># Waiting for user trigger...</span>", unsafe_allow_html=True)
        
        if run_sim:
            with st.spinner("Initializing sandbox telemetry pipeline..."):
                time.sleep(sim_speed * 0.5)
            
            # Step 1: User Input
            st.markdown("<p class='term-step1'><b>📥 STEP 1: USER INPUT Sensed</b></p>", unsafe_allow_html=True)
            st.markdown(f"<p class='term-text'>&gt;&gt; Captured target parameters for pipeline: [<b>{flow_choice.split()[1]}</b>]</p>", unsafe_allow_html=True)
            time.sleep(sim_speed)
            
            # Step 2: AI Processing & Inference
            st.markdown("<p class='term-step2'><b>🧠 STEP 2: BACKEND AI PROCESSING</b></p>", unsafe_allow_html=True)
            st.markdown("<p class='term-text'>&gt;&gt; Triggering feature extraction & model inference nodes...</p>", unsafe_allow_html=True)
            time.sleep(sim_speed)
            
            # Step 3: Database Storage or Retrieval
            st.markdown("<p class='term-step3'><b>💾 STEP 3: DATABASE / MODEL HANDSHAKE</b></p>", unsafe_allow_html=True)
            st.markdown("<p class='term-text'>&gt;&gt; Validating security keys & state values. Persisting records...</p>", unsafe_allow_html=True)
            time.sleep(sim_speed)
            
            # Step 4: Display Output
            st.markdown("<p class='term-step4'><b>📺 STEP 4: RESULT DISPLAY UPDATED</b></p>", unsafe_allow_html=True)
            st.markdown("<p class='term-text'>&gt;&gt; Recompiled views & dynamic responsive graphs successfully.</p>", unsafe_allow_html=True)
            time.sleep(sim_speed)
            
            # Step 5: User Action complete
            st.markdown("<p class='term-step5'><b>🎯 STEP 5: COMPLETED USER JOURNEY</b></p>", unsafe_allow_html=True)
            st.markdown("<p class='term-step1'>&gt;&gt; <b>Pipeline Status: SUCCESS (200 OK)</b> 🎉</p>", unsafe_allow_html=True)
            st.toast("Workflow execution simulation complete! 🌱", icon="✔️")
            
        st.markdown("</div>", unsafe_allow_html=True)
        st.markdown("</div>", unsafe_allow_html=True)

# ==========================================
# TAB 8: PROFILE & AUTHENTICATION
# ==========================================
if active_tab == "👤 Profile & Auth":
    st.subheader("👤 EcoFriend Profile & Simulated Authentication")
    st.write("Manage active login profiles, inspect saved plant species, and unlock eco achievements.")
    
    col_p1, col_p2 = st.columns([1, 1.5])
    
    with col_p1:
        st.markdown("<div class='premium-card'>", unsafe_allow_html=True)
        st.markdown("#### 🔐 Sandbox Sign-In Portal")
        
        st.session_state.auth_logged_in = st.toggle("Simulate Account Logged-In State", value=st.session_state.auth_logged_in)
        
        if st.session_state.auth_logged_in:
            profile_name = st.text_input("Active Username Display", value=st.session_state.auth_user_name)
            if profile_name != st.session_state.auth_user_name:
                st.session_state.auth_user_name = profile_name
                st.rerun()
                
            email_addr = st.text_input("Email", "arjun.patel@ecofriend.org")
            st.success(f"✔️ Active session secure with: OTP / Google One-Tap")
            if st.button("Simulate Log-Out"):
                st.session_state.auth_logged_in = False
                st.rerun()
        else:
            st.info("Log in or create a sandbox account below to save plant arrays and participate in regional campaigns.")
            sim_email = st.text_input("Enter Email Address", "your_email@gmail.com")
            sim_method = st.selectbox("Method", ["Email Magic Link", "Google Single Sign-On Account", "OTP Mobile Code"])
            
            if st.button("Establish Mock Secure Auth Session"):
                st.session_state.auth_logged_in = True
                st.session_state.auth_user_name = sim_email.split("@")[0].capitalize()
                st.toast("Success logged in!", icon="🎉")
                st.rerun()
        st.markdown("</div>", unsafe_allow_html=True)
        
    with col_p2:
        st.markdown("<div class='premium-card'>", unsafe_allow_html=True)
        st.markdown("#### 💾 Registered Active Plant Catalog")
        st.write("You have successfully registered the following varieties inside your tracking matrix:")
        
        for pl in st.session_state.user_saved_plants:
            st.markdown(f"- 🌿 **{pl}**")
            
        new_tag = st.text_input("Save new custom plant name to records:", "")
        if st.button("Add to Active Records"):
            if new_tag and new_tag not in st.session_state.user_saved_plants:
                st.session_state.user_saved_plants.append(new_tag)
                st.toast(f"Added {new_tag} directly into user inventory!", icon="💾")
                st.rerun()
        st.markdown("</div>", unsafe_allow_html=True)
        
        st.markdown("<div class='premium-card'>", unsafe_allow_html=True)
        st.markdown("#### 🛡️ Unlocked Sustainability Awards & Eco-Badges")
        col_b1, col_b2, col_b3 = st.columns(3)
        with col_b1:
            st.markdown("""
            <div style="background-color:#FEF3C7; text-align:center; padding:1rem; border-radius:12px; border:1px solid #F59E0B;">
                <span style="font-size:2rem;">🌟</span>
                <p style="margin:0.25rem 0 0 0; font-size:0.75rem; font-weight:bold; color:#78350F;">GOLDEN SPROUT</p>
                <caption style="font-size:0.65rem;">Plantation catalog seed added</caption>
            </div>
            """, unsafe_allow_html=True)
        with col_b2:
            st.markdown("""
            <div style="background-color:#E0F2FE; text-align:center; padding:1rem; border-radius:12px; border:1px solid #38BDF8;">
                <span style="font-size:2rem;">🛡️</span>
                <p style="margin:0.25rem 0 0 0; font-size:0.75rem; font-weight:bold; color:#0369A1;">WATER GUARDIAN</p>
                <caption style="font-size:0.65rem;">Watering checklist filled for 7d</caption>
            </div>
            """, unsafe_allow_html=True)
        with col_b3:
            st.markdown("""
            <div style="background-color:#F5F5F4; text-align:center; padding:1rem; border-radius:12px; border:1px solid #78716C; opacity: 0.5;">
                <span style="font-size:2rem;">🔒</span>
                <p style="margin:0.25rem 0 0 0; font-size:0.75rem; font-weight:bold; color:#44403C;">FOREST MASTER</p>
                <caption style="font-size:0.65rem;">Unlock this at 1500 GP</caption>
            </div>
            """, unsafe_allow_html=True)
        st.markdown("</div>", unsafe_allow_html=True)

# ==============================================================================
# PREMIUM LOWER NAVIGATION SYSTEM DOCK
# ==============================================================================
st.markdown("<p style='margin-top: 4rem;'></p>", unsafe_allow_html=True)  # Add generous spacing before bottom bar
st.markdown("---")

st.markdown("""
<div style="background-color: #EBF8FF; padding: 1rem; border-radius: 14px; border: 1px solid #BEE3F8; margin-bottom:1.5rem; text-align: center;">
    <h3 style="margin: 0; color: #2B6CB0 !important; font-size: 1.15rem;">📱 EcoFriend Bottom Navigation Dock</h3>
    <p style="margin: 0; font-size: 0.8rem; color: #4A5568;">Click a tab below to jump directly to any active screen</p>
</div>
""", unsafe_allow_html=True)

nav_cols = st.columns(9)
nav_icons = {
    "🏠 Home Hub": "🏠 Home",
    "🎯 Smart Recommendations": "🎯 Recs",
    "🔍 Leaf & Soil Diagnostic": "🔍 Scan",
    "🤖 PrakritiMitra Chat": "🤖 Chat",
    "📈 Water & Growth": "📈 Growth",
    "📚 Learning Hub": "📚 Learn",
    "🌍 Community Grid": "🌍 Social",
    "🔄 Flow & Architecture": "🔄 Flow",
    "👤 Profile & Auth": "👤 Profile"
}

for idx, tab_name in enumerate(tabs_options):
    with nav_cols[idx]:
        is_active = (active_tab == tab_name)
        short_name = nav_icons[tab_name]
        
        # Streamlit button that triggers state synchronized navigation
        if is_active:
            st.markdown(f"""
            <div style="text-align: center; border-bottom: 3px solid #2E7D32; padding-bottom: 2px;">
                <p style="margin: 0; font-size: 0.8rem; font-weight: bold; color: #2E7D32;">{short_name.split()[0]}</p>
            </div>
            """, unsafe_allow_html=True)
            st.button(short_name, key=f"bottom_active_btn_{idx}", use_container_width=True, disabled=True)
        else:
            if st.button(short_name, key=f"bottom_nav_btn_{idx}", use_container_width=True):
                st.session_state.current_tab = tab_name
                st.session_state.navigation_radio_selector = tab_name
                st.rerun()
